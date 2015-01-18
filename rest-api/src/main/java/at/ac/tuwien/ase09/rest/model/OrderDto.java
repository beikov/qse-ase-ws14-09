package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderType;

public class OrderDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private OrderType orderType;
	private OrderAction orderAction;
	private Calendar validFrom;
	private Calendar validTo;
	private long portfolioId;
	private long valuePaperId;
	private int volume;
	private BigDecimal stopLimit;
	private BigDecimal limit;
	
	public OrderDto() { }
	
	

	public OrderDto(OrderType orderType, OrderAction orderAction,
			Calendar validFrom, Calendar validTo, long portfolioId,
			long valuePaperId, int volume, BigDecimal stopLimit,
			BigDecimal limit) {
		super();
		this.orderType = orderType;
		this.orderAction = orderAction;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.portfolioId = portfolioId;
		this.valuePaperId = valuePaperId;
		this.volume = volume;
		this.stopLimit = stopLimit;
		this.limit = limit;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public OrderAction getOrderAction() {
		return orderAction;
	}

	public void setOrderAction(OrderAction orderAction) {
		this.orderAction = orderAction;
	}

	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

	public Calendar getValidTo() {
		return validTo;
	}

	public void setValidTo(Calendar validTo) {
		this.validTo = validTo;
	}

	public long getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(long portfolioId) {
		this.portfolioId = portfolioId;
	}

	public long getValuePaperId() {
		return valuePaperId;
	}

	public void setValuePaperId(long valuePaperId) {
		this.valuePaperId = valuePaperId;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public BigDecimal getStopLimit() {
		return stopLimit;
	}

	public void setStopLimit(BigDecimal stopLimit) {
		this.stopLimit = stopLimit;
	}

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}

}
