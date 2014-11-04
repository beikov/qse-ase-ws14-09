package at.ac.tuwien.ase09.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_STOCK)
public class Stock extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private String certificatePageLink;
	private String isin;
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.STOCK;
	}

	public String getCertificatePageLink() {
		return certificatePageLink;
	}

	public void setCertificatePageLink(String certificatePageLink) {
		this.certificatePageLink = certificatePageLink;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}
	
}
