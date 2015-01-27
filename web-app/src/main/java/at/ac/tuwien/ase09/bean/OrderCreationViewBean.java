package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Calendar;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.PortfolioContext;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;
import at.ac.tuwien.ase09.service.OrderService;


@ViewScoped
@Named
public class OrderCreationViewBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private OrderAction orderAction;
	private OrderType orderType = OrderType.MARKET;
	private ValuePaper valuePaper;
	private BigDecimal limit = new BigDecimal(0);
	private BigDecimal stopLimit = new BigDecimal(0);
	private int volume;
	private Date validFrom = new Date();
	private Date validTo = new Date();
	
	@Inject
	private OrderService orderService;
	@Inject
	private PortfolioContext portfolioContext;
	
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

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
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

	public String getCurrencySymbol(){
		if(valuePaper instanceof Stock){
			return ((Stock) valuePaper).getCurrency().getCurrencyCode();
		}else if(valuePaper instanceof Fund){
			return ((Fund) valuePaper).getCurrency().getCurrencyCode();
		}else {
			return "";
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

	public String create(){
		// TODO: validate that portfolio context was chosen
		Calendar validFromCalendar = Calendar.getInstance();
		validFromCalendar.setTime(validFrom);
		Calendar validToCalendar = null;
		if(validTo != null){
			validToCalendar = Calendar.getInstance();
			validToCalendar.setTime(validTo);
		}
		if(orderType == OrderType.LIMIT){
			orderService.createLimitOrder(orderAction, validFromCalendar, validToCalendar, portfolioContext.getContextId(), volume, valuePaper.getId(), stopLimit, limit);
			LimitOrder limitOrder = new LimitOrder();
			limitOrder.setLimit(limit);
			limitOrder.setStopLimit(stopLimit);
		}else{
			orderService.createMarketOrder(orderAction, validFromCalendar, validToCalendar, portfolioContext.getContextId(), volume, valuePaper.getId());
		}
		
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Order für " + valuePaper.getName() + " erfolgreich erstellt"));
		return "/portfolio/view?faces-redirect=true&portfolioId=" + portfolioContext.getContextId();
	}

}
