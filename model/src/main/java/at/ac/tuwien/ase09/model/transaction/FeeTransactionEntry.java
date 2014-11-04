package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(TransactionType.TYPE_FEE)
public class FeeTransactionEntry extends TransactionEntry {
	private static final long serialVersionUID = 1L;

	private String description;
	
	@Override
	@Transient
	public TransactionType getType() {
		return TransactionType.FEE;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
