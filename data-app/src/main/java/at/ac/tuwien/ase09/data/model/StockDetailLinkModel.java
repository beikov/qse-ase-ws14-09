package at.ac.tuwien.ase09.data.model;

import java.io.Serializable;

public class StockDetailLinkModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String detailLink;
	private final String companyName;
	private final String isin;
	private final String tickerSymbol;

	public StockDetailLinkModel(String detailLink,
			String companyName, String isin, String tickerSymbol) {
		super();
		this.detailLink = detailLink;
		this.companyName = companyName;
		this.isin = isin;
		this.tickerSymbol = tickerSymbol;
	}
	public String getDetailLink() {
		return detailLink;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getIsin() {
		return isin;
	}
	public String getTickerSymbol() {
		return tickerSymbol;
	}
	
	
}
