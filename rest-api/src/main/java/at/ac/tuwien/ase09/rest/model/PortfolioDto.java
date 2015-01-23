package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class PortfolioDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private Currency currency;
	private BigDecimal currentCapital;
	private BigDecimal costValue;
	private BigDecimal currentValue;
	private BigDecimal orderFee;

	public PortfolioDto(){}

	public PortfolioDto(Long id, String name, Currency currency,
			BigDecimal currentCapital, BigDecimal costValue,
			BigDecimal currentValue, BigDecimal orderFee) {
		super();
		this.id = id;
		this.name = name;
		this.currency = currency;
		this.currentCapital = currentCapital;
		this.costValue = costValue;
		this.currentValue = currentValue;
		this.orderFee = orderFee;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getCurrentCapital() {
		return currentCapital;
	}

	public void setCurrentCapital(BigDecimal currentCapital) {
		this.currentCapital = currentCapital;
	}

	public BigDecimal getCostValue() {
		return costValue;
	}

	public void setCostValue(BigDecimal costValue) {
		this.costValue = costValue;
	}

	public BigDecimal getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(BigDecimal currentValue) {
		this.currentValue = currentValue;
	}

	public BigDecimal getOrderFee() {
		return orderFee;
	}

	public void setOrderFee(BigDecimal orderFee) {
		this.orderFee = orderFee;
	}
}
