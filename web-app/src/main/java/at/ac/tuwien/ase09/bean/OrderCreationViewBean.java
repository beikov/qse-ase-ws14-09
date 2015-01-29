package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.PortfolioContext;
import at.ac.tuwien.ase09.currency.CurrencyConversionService;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.service.OrderService;
import at.ac.tuwien.ase09.validator.OrderValidator;


@ViewScoped
@Named
public class OrderCreationViewBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private OrderAction orderAction;
	private OrderType orderType = OrderType.MARKET;
	private ValuePaper valuePaper;
	private BigDecimal limit = new BigDecimal(0);
	private BigDecimal stopLimit = new BigDecimal(0);
	private int volume = 1;
	private Date validFrom = new Date();
	private Date validTo;
	private BigDecimal lastPrice;
	private BigDecimal lastHistoricPrice;
	private Portfolio portfolio;
	private BigDecimal conversionRate = BigDecimal.ONE;
	
	@Inject
	private OrderService orderService;
	@Inject
	private PortfolioContext portfolioContext;
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	@Inject
	private CurrencyConversionService currencyConversionService;
	
	@PostConstruct
	private void init(){
		if(portfolioContext.getContextId() != null){
			portfolio = portfolioDataAccess.getPortfolioById(portfolioContext.getContextId());
		}
	}
	
	public OrderAction getOrderAction() {
		return orderAction;
	}

	public void setOrderAction(OrderAction orderAction) {
		this.orderAction = orderAction;
	}
	
	public String getOrderActionStr(){
		if(orderAction == OrderAction.BUY){
			return "Kauf";
		}else{
			return "Verkauf";
		}
	}
	
	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public OrderType[] getOrderTypes(){
		return OrderType.values();
	}

	public ValuePaper getValuePaper() {
		return valuePaper;
	}
	
	public BigDecimal getAbsolutePriceChange(){
		return lastPrice.subtract(lastHistoricPrice);
	}
	
	public BigDecimal getRelativePriceChange(){
		return getAbsolutePriceChange().divide(lastHistoricPrice, RoundingMode.HALF_DOWN);
	}

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
		this.lastPrice = valuePaperPriceEntryDataAccess.getLatestPrice(valuePaper.getId());
		this.lastHistoricPrice = valuePaperPriceEntryDataAccess.getLatestHistoryEntry(valuePaper.getId()).getClosingPrice();
		this.limit = new BigDecimal(lastPrice.floatValue());
		this.stopLimit = new BigDecimal(lastPrice.floatValue());
		
		if(portfolio != null && !portfolio.getCurrentCapital().getCurrency().equals(getCurrency())) {
			conversionRate = currencyConversionService.getConversionRate(getCurrency(), portfolio.getCurrentCapital().getCurrency());
		}
	}
	
	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

	public BigDecimal getStopLimit() {
		return stopLimit;
	}

	public void setStopLimit(BigDecimal stopLimit) {
		this.stopLimit = stopLimit;
	}

	public Currency getCurrency(){
		if(valuePaper instanceof Stock){
			return ((Stock) valuePaper).getCurrency();
		}else if(valuePaper instanceof Fund){
			return ((Fund) valuePaper).getCurrency();
		}else {
			return null;
		}
	}
	
	public boolean isLimitOrder(){
		return orderType == orderType.LIMIT;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	
	public BigDecimal getEstimatedCosts(){
		return CurrencyConversionService.convert(lastPrice.multiply(new BigDecimal(volume)), conversionRate);
	}
	
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public String create(){
		// validate that portfolio context was chosen
		if(portfolio == null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Kein Portfolio ausgewählt", null));
			return "";
		}
		
		Calendar validFromCalendar = Calendar.getInstance();
		validFromCalendar.setTime(validFrom);
		Calendar validToCalendar = null;
		if(validTo != null){
			validToCalendar = Calendar.getInstance();
			validToCalendar.setTime(validTo);
		}
		
		// execute order validation
		int result = OrderValidator.validateOrder(orderType, orderAction, lastPrice, limit, stopLimit, validFromCalendar, validToCalendar);
		if(result != OrderValidator.OK){
			// validation failed
			String message;
	        switch (result) {
	            case OrderValidator.LIMIT_REQUIRED: message = "Limit erforderlich"; break;
	            case OrderValidator.LIMIT_EQUAL_OR_HIGHER_THAN_PRICE: message = "Ihr Limit muss unter dem aktuellen Preis liegen"; break;
	            case OrderValidator.LIMIT_EQUAL_OR_LOWER_THAN_PRICE: message = "Ihr Limit muss über dem aktuellen Preis liegen"; break;
	            case OrderValidator.LIMIT_HIGHER_THAN_STOP_LIMIT: message = "Ihr Limit muss kleiner oder gleich Ihrem Stop Limit sein"; break;
	            case OrderValidator.LIMIT_LOWER_THAN_STOP_LIMIT: message = "Ihr Limit muss größer oder gleich Ihrem Stop Limit sein"; break;
	            case OrderValidator.STOP_LIMIT_EQUAL_OR_HIGHER_THAN_PRICE: message = "Ihr Limit muss kleiner oder gleich Ihrem Stop Limit sein"; break;
		        case OrderValidator.STOP_LIMIT_EQUAL_OR_LOWER_THAN_PRICE: message = "Ihr Stop Limit muss unter dem aktuellen Preis liegen"; break;
		        case OrderValidator.VALID_FROM_REQUIRED: message = "Ihr Stop Limit muss über dem aktuellen Preis liegen"; break;
		        case OrderValidator.VALID_TO_BEFORE_VALID_FROM: message = "'Gültig von' erforderlich"; break;
		        case OrderValidator.VALID_TO_BEFORE_NOW: message = "'Gültig von' muss bevor 'Gültig bis' liegen"; break;
		        default: throw new IllegalArgumentException("Unknown validation code [" + result + "]");
	        }
	        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
	        return "";
		}else{
			// validation ok
			if(orderType == OrderType.LIMIT){
				orderService.createLimitOrder(orderAction, validFromCalendar, validToCalendar, portfolio.getId(), volume, valuePaper.getId(), stopLimit, limit);
				LimitOrder limitOrder = new LimitOrder();
				limitOrder.setLimit(limit);
				limitOrder.setStopLimit(stopLimit);
			}else{
				orderService.createMarketOrder(orderAction, validFromCalendar, validToCalendar, portfolio.getId(), volume, valuePaper.getId());
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order für " + valuePaper.getName() + " erfolgreich erstellt"));
			return "/portfolio/view?faces-redirect=true&portfolioId=" + portfolioContext.getContextId();
		}
	}
	
}
