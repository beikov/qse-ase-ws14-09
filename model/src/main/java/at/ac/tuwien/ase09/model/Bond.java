package at.ac.tuwien.ase09.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(ValuePaperType.TYPE_BOND)
public class Bond extends ValuePaper {
	private static final long serialVersionUID = 1L;

	private String wkn;

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	@Override
	@Transient
	public ValuePaperType getType() {
		return ValuePaperType.BOND;
	}
	
	
}
