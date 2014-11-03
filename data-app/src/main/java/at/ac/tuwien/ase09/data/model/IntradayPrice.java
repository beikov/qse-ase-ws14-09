package at.ac.tuwien.ase09.data.model;

import java.math.BigDecimal;
import java.util.Currency;

public class IntradayPrice {
	private final String isin;
	private final Currency currency;
	private final BigDecimal price;
	
	public IntradayPrice(String isin, Currency currency, BigDecimal price) {
		super();
		this.isin = isin;
		this.currency = currency;
		this.price = price;
	}
	
	public String getIsin() {
		return isin;
	}
	public Currency getCurrency() {
		return currency;
	}
	public BigDecimal getPrice() {
		return price;
	}

	
}
