package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

public class PortfolioValuePaperDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String name;
	private Currency currency;
	private BigDecimal lastPrice;
	private BigDecimal previousDayPrice;
	private BigDecimal buyPrice;
	private int volume;
	
	public PortfolioValuePaperDto() { }
			
	public PortfolioValuePaperDto(long id, String name, Currency currency,
			BigDecimal lastPrice, BigDecimal previousDayPrice,
			BigDecimal buyPrice, int volume) {
		super();
		this.id = id;
		this.name = name;
		this.currency = currency;
		this.lastPrice = lastPrice;
		this.previousDayPrice = previousDayPrice;
		this.buyPrice = buyPrice;
		this.volume = volume;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
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

	public BigDecimal getBuyPrice() {
		return buyPrice;
	}

	public int getVolume() {
		return volume;
	}

}
