package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(TransactionType.TYPE_FEE)
public class FeeTransactionEntry extends TransactionEntry {

	@Override
	public TransactionType getType() {
		return TransactionType.FEE;
	}
}
