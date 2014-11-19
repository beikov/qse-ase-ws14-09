package at.ac.tuwien.ase09.model;

import java.util.Currency;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_STOCK)
public class Stock extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private Currency currency;
	private String country;
	private String historicPricesPageUrl;
	private String boerseCertificatePageUrl;
	private String finanzenCertificatePageUrl;
	private String index;
	
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.STOCK;
	}

	public String getBoerseCertificatePageUrl() {
		return boerseCertificatePageUrl;
	}

	public void setBoerseCertificatePageUrl(String boerseCertificatePageUrl) {
		this.boerseCertificatePageUrl = boerseCertificatePageUrl;
	}

	public String getFinanzenCertificatePageUrl() {
		return finanzenCertificatePageUrl;
	}

	public void setFinanzenCertificatePageUrl(String finanzenCertificatePageUrl) {
		this.finanzenCertificatePageUrl = finanzenCertificatePageUrl;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getHistoricPricesPageUrl() {
		return historicPricesPageUrl;
	}

	public void setHistoricPricesPageUrl(String historicPricesPageUrl) {
		this.historicPricesPageUrl = historicPricesPageUrl;
	}
	
}
