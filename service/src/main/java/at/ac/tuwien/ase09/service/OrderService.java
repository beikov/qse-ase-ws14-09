package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.MarketOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderStatus;

/**
 * TODO: insert orders into event processing
 * @author Moritz
 *
 */
public class OrderService {
	@Inject
	private EntityManager em;
	
	public MarketOrder createMarketOrder(OrderAction orderAction, Calendar validFrom, Calendar validTo, long portfolioId, int volume, long valuePaperId){
		MarketOrder marketOrder = createOrder(MarketOrder.class, orderAction, validFrom, validTo, portfolioId, volume, valuePaperId);		
		marketOrder.setStatus(OrderStatus.OPEN);
		em.persist(marketOrder);
		return marketOrder;
	}
	
	public LimitOrder createLimitOrder(OrderAction orderAction, Calendar validFrom, Calendar validTo, long portfolioId, int volume, long valuePaperId, BigDecimal stopLimit, BigDecimal limit){
		LimitOrder limitOrder = createOrder(LimitOrder.class, orderAction, validFrom, validTo, portfolioId, volume, valuePaperId);
		limitOrder.setStopLimit(stopLimit);
		limitOrder.setLimit(limit);
		
		em.persist(limitOrder);
		return limitOrder;
	}
	
	private <T extends Order> T createOrder(Class<T> clazz, OrderAction orderAction, Calendar validFrom, Calendar validTo, long portfolioId, int volume, long valuePaperId){
		try {
			T order = clazz.newInstance();
			order.setOrderAction(orderAction);
			order.setValidFrom(validFrom);
			order.setValidTo(validTo);
			order.setPortfolio(em.getReference(Portfolio.class, portfolioId));
			order.setVolume(volume);
			order.setValuePaper(em.getReference(ValuePaper.class, valuePaperId));
			order.setStatus(OrderStatus.OPEN);
			return order;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
