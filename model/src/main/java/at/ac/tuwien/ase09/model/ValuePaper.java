package at.ac.tuwien.ase09.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "VALUEPAPER_TYPE")
public abstract class ValuePaper extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private String name;
	private String isin;
	private String detailUrl;
	private String historicPricesPageUrl;
	

	@Column(unique=true)
	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}
	
	@Transient
	public abstract ValuePaperType getType();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("[type=%s, code=%s, name=%s]", getType(), isin, name);
	}

	@Column(columnDefinition="TEXT")
	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	@Column(columnDefinition="TEXT")
	public String getHistoricPricesPageUrl() {
		return historicPricesPageUrl;
	}

	public void setHistoricPricesPageUrl(String historicPricesPageUrl) {
		this.historicPricesPageUrl = historicPricesPageUrl;
	}
	
}
