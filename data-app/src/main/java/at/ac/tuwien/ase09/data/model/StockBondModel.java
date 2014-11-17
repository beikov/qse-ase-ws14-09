package at.ac.tuwien.ase09.data.model;

public class StockBondModel {
	private final String name;
	private final String isin;
	private final String historicPricesPageUrl;
	private final String detailUrl;
	private final String baseValueIsin;
	
	public StockBondModel(String name, String isin,
			String historicPricesPageUrl, String detailUrl, String baseValueIsin) {
		super();
		this.name = name;
		this.isin = isin;
		this.historicPricesPageUrl = historicPricesPageUrl;
		this.detailUrl = detailUrl;
		this.baseValueIsin = baseValueIsin;
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
	
}
