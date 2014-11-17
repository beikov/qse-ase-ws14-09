package at.ac.tuwien.ase09.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_BOND)
public class StockBond extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private Stock baseStock;
	private String detailUrl;
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.BOND;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	public Stock getBaseStock() {
		return baseStock;
	}

	public void setBaseStock(Stock baseStock) {
		this.baseStock = baseStock;
	}

	public String getDetailUrl() {
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	
}
