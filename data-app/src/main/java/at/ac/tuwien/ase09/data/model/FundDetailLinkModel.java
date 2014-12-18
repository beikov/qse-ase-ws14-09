package at.ac.tuwien.ase09.data.model;

import java.io.Serializable;

public class FundDetailLinkModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String detailUrl;
	private final String key;
	
	public FundDetailLinkModel(String detailUrl, String key) {
		super();
		this.detailUrl = detailUrl;
		this.key = key;
	}

	public String getDetailUrl() {
		return detailUrl;
	}
	
	public String getKey() {
		return key;
	}
	
}
