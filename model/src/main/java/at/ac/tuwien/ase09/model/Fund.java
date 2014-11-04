package at.ac.tuwien.ase09.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_FUND)
public class Fund extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private String isin;

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}
	
	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.FUND;
	}
	
}
