package at.ac.tuwien.ase09.model;

import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "VALUEPAPER_TYPE")
public abstract class ValuePaper extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private String name;
	/**
	 * The code is either an ISIN or a symbol (for US stocks)
	 */
	private String code;
	private String detailUrl;	// for extraction
	private String historicPricesPageUrl; // for extraction
	
	
	@Transient
	public abstract ValuePaperType getType();
	
	@Column(nullable=false, unique=true)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return String.format("[type=%s, code=%s, name=%s]", getType(), code, name);
	}

	@Lob
	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	@Lob
	public String getHistoricPricesPageUrl() {
		return historicPricesPageUrl;
	}

	public void setHistoricPricesPageUrl(String historicPricesPageUrl) {
		this.historicPricesPageUrl = historicPricesPageUrl;
	}
	
}
