package at.ac.tuwien.ase09.model;

import java.util.Currency;

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
	private Currency currency;
	private String isin;

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

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return String.format("[type=%s, code=%s, name=%s]", getType(), isin, name);
	}
	
	
}
