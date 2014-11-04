package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(TransactionType.TYPE_TAX)
public class TaxTransactionEntry extends TransactionEntry {
	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public TransactionType getType() {
		return TransactionType.TAX;
	}
}
