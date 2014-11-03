package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(TransactionType.TYPE_TAX)
public class TaxTransactionEntry extends TransactionEntry {

	@Override
	public TransactionType getType() {
		return TransactionType.TAX;
	}
}
