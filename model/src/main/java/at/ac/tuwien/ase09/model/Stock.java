package at.ac.tuwien.ase09.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_STOCK)
public class Stock extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private String certificatePageUrl;
	private String index;
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.STOCK;
	}

	public String getCertificatePageUrl() {
		return certificatePageUrl;
	}

	public void setCertificatePageUrl(String certificatePageUrl) {
		this.certificatePageUrl = certificatePageUrl;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
}
