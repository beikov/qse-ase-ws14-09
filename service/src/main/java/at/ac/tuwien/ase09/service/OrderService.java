package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import at.ac.tuwien.ase09.currency.CurrencyConversionService;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.event.Deleted;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.MarketOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderStatus;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.model.transaction.OrderFeeTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TaxTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.validator.OrderValidator;

/**
 * @author Moritz
 *
 */
@Stateless
public class OrderService extends AbstractService {
	
	@Inject
	@Added
	private Event<Order> orderAdded;
	@Inject
	@Deleted
	private Event<Order> orderDeleted;
	@Inject
	@Deleted
	private Event<TransactionEntry> transactionAdded;
	@Inject
	private CurrencyConversionService currencyConversionService;
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;
	
	public MarketOrder createMarketOrder(OrderAction orderAction, Calendar validFrom, Calendar validTo, long portfolioId, int volume, long valuePaperId) {
		BigDecimal currentPrice = valuePaperPriceEntryDataAccess.getLatestPrice(valuePaperId);
		OrderValidator.validateOrder(OrderType.MARKET, orderAction, currentPrice, null, null, validFrom, validTo);
		MarketOrder marketOrder = createOrder(new MarketOrder(), orderAction, validFrom, validTo, portfolioId, volume, valuePaperId);
		persistOrder(marketOrder);
		return marketOrder;
	}
	
	public LimitOrder createLimitOrder(OrderAction orderAction, Calendar validFrom, Calendar validTo, long portfolioId, int volume, long valuePaperId, BigDecimal stopLimit, BigDecimal limit) {
		BigDecimal currentPrice = valuePaperPriceEntryDataAccess.getLatestPrice(valuePaperId);
		OrderValidator.validateOrder(OrderType.LIMIT, orderAction, currentPrice, limit, stopLimit, validFrom, validTo);
		LimitOrder limitOrder = createOrder(new LimitOrder(), orderAction, validFrom, validTo, portfolioId, volume, valuePaperId);
		limitOrder.setStopLimit(stopLimit);
		limitOrder.setLimit(limit);
		persistOrder(limitOrder);
		return limitOrder;
	}
	
	/**
	 * 1. Check if there is enough capital for the order fee
	 * 2. Persist order
	 * 3. Persist fee entry
	 * 4. Update capital
	 * 5. Register order in event processing
	 * 
	 * @param order
	 */
	private void persistOrder(Order order) {
		Portfolio portfolio = em.find(Portfolio.class, order.getPortfolio().getId(), LockModeType.PESSIMISTIC_WRITE);
		Money orderFee = portfolio.getSetting().getOrderFee();
		BigDecimal newCapitalValue = portfolio.getCurrentCapital().getValue().subtract(orderFee.getValue());
		if (newCapitalValue.compareTo(BigDecimal.ZERO) < 0) {
			throw new AppException("Can not create the order because the user does not have enough capital!");
		}
		
		portfolio.getCurrentCapital().setValue(newCapitalValue);
		
		OrderFeeTransactionEntry entry = new OrderFeeTransactionEntry();
		entry.setOrder(order);
		entry.setPortfolio(order.getPortfolio());
		entry.setValue(orderFee);
		em.persist(order);
		em.persist(entry);
		orderAdded.fire(order);
		transactionAdded.fire(entry);
	}

	/**
	 * 1. Set expired state of order
	 * 
	 * @param orderId
	 */
	public void expireOrder(Long orderId) {
		Order order = em.find(Order.class, orderId);
		
		if (order.getStatus() != OrderStatus.OPEN) {
			return;
		}
		
		order.setStatus(OrderStatus.EXPIRED);
		em.merge(order);
		em.flush();
	}
	
	/**
	 * 1. Set cancel state of order
	 * 2. Cancel order in event processing
	 * 
	 * @param orderId
	 */
	public void cancelOrder(Long orderId) {
		Order order = em.find(Order.class, orderId);
		
		if (order.getStatus() != OrderStatus.OPEN) {
			return;
		}
		
		order.setStatus(OrderStatus.CANCELED);
		em.merge(order);
		em.flush();
		orderDeleted.fire(order);
	}

	/**
	 * 1. Check if there is enough capital for a buy order
	 * 2. Persist order entry
	 * 3. If it's a sell order, persist tax entry
	 * 4. Update capital
	 * 5. Set closed state of order
	 * 
	 * @param orderId
	 */
	public void closeOrder(Long orderId, BigDecimal price) {
		Order order = em.find(Order.class, orderId);
		
		if (order.getStatus() != OrderStatus.OPEN) {
			return;
		}

		em.setFlushMode(FlushModeType.COMMIT);
		
		Portfolio portfolio = em.find(Portfolio.class, order.getPortfolio().getId(), LockModeType.PESSIMISTIC_WRITE);
		BigDecimal capitalDelta = BigDecimal.ZERO;
		OrderTransactionEntry entry = new OrderTransactionEntry();
		entry.setOrder(order);
		entry.setPortfolio(order.getPortfolio());
		
		BigDecimal entryMoneyValue = price.multiply(BigDecimal.valueOf(order.getVolume()));
		Money entryValue;
		
		if (order.getValuePaper() instanceof Stock) {
			entryValue = new Money(entryMoneyValue, ((Stock) order.getValuePaper()).getCurrency());
		} else if (order.getValuePaper() instanceof Fund) {
			entryValue = new Money(entryMoneyValue, ((Fund) order.getValuePaper()).getCurrency());
		} else {
			throw new IllegalArgumentException("Can not buy stock bonds!");
		}
		
		entry.setValue(convertCurrency(entryValue, order.getPortfolio().getCurrentCapital().getCurrency()));
		em.persist(entry);
		transactionAdded.fire(entry);
		
		if (order.getOrderAction() == OrderAction.SELL) {
			TaxTransactionEntry tax = new TaxTransactionEntry();
			tax.setPortfolio(order.getPortfolio());
			BigDecimal taxPercent = order.getPortfolio().getSetting().getCapitalReturnTax();
			BigDecimal taxMoneyValue = taxPercent.multiply(entry.getValue().getValue());
			Money taxValue = new Money(taxMoneyValue, entry.getValue().getCurrency());
			tax.setValue(taxValue);
			capitalDelta = capitalDelta.add(entry.getValue().getValue());
			capitalDelta = capitalDelta.subtract(taxMoneyValue);
			em.persist(tax);
			// TODO: for taxes too?
//			transactionAdded.fire(tax);
		} else {
			capitalDelta = capitalDelta.subtract(entry.getValue().getValue());
		}
		
		portfolio.getCurrentCapital().setValue(portfolio.getCurrentCapital().getValue().add(capitalDelta));
		
		if (portfolio.getCurrentCapital().getValue().compareTo(BigDecimal.ZERO) < 0) {
			em.clear();
			order.setStatus(OrderStatus.CANCELED);
		} else {
			order.setStatus(OrderStatus.CLOSED);
		}

		em.merge(order);
		em.flush();
	}
	
	private Money convertCurrency(Money foreignValue, Currency targetCurrency) {
		if (foreignValue.getCurrency().equals(targetCurrency)) {
			return foreignValue;
		}
		
		BigDecimal conversionRate = currencyConversionService.getConversionRate(foreignValue.getCurrency(), targetCurrency);
		return new Money(foreignValue.getValue().multiply(conversionRate), targetCurrency);
	}
	
	private <T extends Order> T createOrder(T order, OrderAction orderAction, Calendar validFrom, Calendar validTo, long portfolioId, int volume, long valuePaperId) {
		order.setOrderAction(orderAction);
		order.setValidFrom(validFrom);
		order.setValidTo(validTo);
		order.setPortfolio(em.getReference(Portfolio.class, portfolioId));
		order.setVolume(volume);
		order.setValuePaper(em.getReference(ValuePaper.class, valuePaperId));
		order.setStatus(OrderStatus.OPEN);
		return order;
	}
}
