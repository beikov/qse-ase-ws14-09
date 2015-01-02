package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class ValuePaperDto implements Serializable {
	private final String name;
	private final String code;
	private final Currency currency;
	private final BigDecimal lastPrice;
	private final BigDecimal previousDayPrice;
	private final BigDecimal dayHighPrice;
	private final BigDecimal dayLowPrice;
	
	public ValuePaperDto(String name, String code, Currency currency,
			BigDecimal lastPrice, BigDecimal previousDayPrice,
			BigDecimal dayHighPrice, BigDecimal dayLowPrice) {
		super();
		this.name = name;
		this.code = code;
		this.currency = currency;
		this.lastPrice = lastPrice;
		this.previousDayPrice = previousDayPrice;
		this.dayHighPrice = dayHighPrice;
		this.dayLowPrice = dayLowPrice;
	}

	public String getName() {
		return name;
	}
	public String getCode() {
		return code;
	}
	public Currency getCurrency() {
		return currency;
	}
	public BigDecimal getLastPrice() {
		return lastPrice;
	}
	public BigDecimal getPreviousDayPrice() {
		return previousDayPrice;
	}
	public BigDecimal getDayHighPrice() {
		return dayHighPrice;
	}
	public BigDecimal getDayLowPrice() {
		return dayLowPrice;
	}
}
