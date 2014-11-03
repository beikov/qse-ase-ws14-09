package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.ValuePaper;

@Entity
@DiscriminatorValue(TransactionType.TYPE_PAYOUT)
public class PayoutTransactionEntry extends TransactionEntry {

	private ValuePaper valuePaper;
	
	@Override
	public TransactionType getType() {
		return TransactionType.PAYOUT;
	}
}
