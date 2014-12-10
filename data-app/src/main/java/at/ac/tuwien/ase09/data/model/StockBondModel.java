package at.ac.tuwien.ase09.data.model;

import java.math.BigDecimal;
import java.util.Calendar;

public class StockBondModel {
	private final String name;
	private final String isin;
	private final String historicPricesPageUrl;
	private final String detailUrl;
	private final String baseValueIsin;
	private final BigDecimal coupon;
	private final BigDecimal emissionPrice;
	private final String emitter;
	private final Calendar emissionDate;
	private final Calendar endDate;
	
	public StockBondModel(String name, String isin,
			String historicPricesPageUrl, String detailUrl,
			String baseValueIsin, BigDecimal coupon, BigDecimal emissionPrice,
			String emitter, Calendar emissionDate, Calendar endDate) {
		super();
		this.name = name;
		this.isin = isin;
		this.historicPricesPageUrl = historicPricesPageUrl;
		this.detailUrl = detailUrl;
		this.baseValueIsin = baseValueIsin;
		this.coupon = coupon;
		this.emissionPrice = emissionPrice;
		this.emitter = emitter;
		this.emissionDate = emissionDate;
		this.endDate = endDate;
	}

	public String getName() {
		return name;
	}

	public String getIsin() {
		return isin;
	}

	public String getHistoricPricesPageUrl() {
		return historicPricesPageUrl;
	}
	
	public String getDetailUrl() {
		return detailUrl;
	}

	public String getBaseValueIsin() {
		return baseValueIsin;
	}

	public BigDecimal getCoupon() {
		return coupon;
	}

	public BigDecimal getEmissionPrice() {
		return emissionPrice;
	}

	public String getEmitter() {
		return emitter;
	}

	public Calendar getEmissionDate() {
		return emissionDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}
	
}
