package at.ac.tuwien.ase09.data.model;

import java.math.BigDecimal;
import java.util.Currency;

public class IntradayPrice {
	private final String isin;
	private final BigDecimal price;
	
	public IntradayPrice(String isin, BigDecimal price) {
		super();
		this.isin = isin;
		this.price = price;
	}
	
	public String getIsin() {
		return isin;
	}
	public BigDecimal getPrice() {
		return price;
	}

	
}
