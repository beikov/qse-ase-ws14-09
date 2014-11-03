package at.ac.tuwien.ase09.model.order;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionType;

@Entity
@DiscriminatorValue(TransactionType.TYPE_ORDER_FEE)
public class OrderFeeTransactionEntry extends TransactionEntry {

	private Order order;
	
	@Override
	public TransactionType getType() {
		return TransactionType.ORDER_FEE;
	}
}
