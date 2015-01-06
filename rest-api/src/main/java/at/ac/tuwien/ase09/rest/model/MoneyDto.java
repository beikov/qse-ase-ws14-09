package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class MoneyDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal value;
	private Currency currency;

	public MoneyDto(){}
	
	public MoneyDto(BigDecimal value, Currency currency){
		this.value = value;
		this.currency = currency;
	}
	
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}
