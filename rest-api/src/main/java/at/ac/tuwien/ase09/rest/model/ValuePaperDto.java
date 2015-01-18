package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class ValuePaperDto implements Serializable {
	private static final long serialVersionUID = -9184479286835690280L;

	private long id;
	private String name;
	private String code;
	private Currency currency;
	private BigDecimal lastPrice;
	private BigDecimal previousDayPrice;
	
	public ValuePaperDto(){ }
	
	public ValuePaperDto(long id, String name, String code, Currency currency,
			BigDecimal lastPrice, BigDecimal previousDayPrice) {
		super();
		this.id = id; 
		this.name = name;
		this.code = code;
		this.currency = currency;
		this.lastPrice = lastPrice;
		this.previousDayPrice = previousDayPrice;
	}
	
	public long getId(){
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getPreviousDayPrice() {
		return previousDayPrice;
	}

	public void setPreviousDayPrice(BigDecimal previousDayPrice) {
		this.previousDayPrice = previousDayPrice;
	}

}
