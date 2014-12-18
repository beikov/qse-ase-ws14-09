package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.transaction.TransactionEntry;

@Entity
@DiscriminatorValue(NotificationType.TYPE_FOLLOWER_TRANSACTION_ADDED)
public class FollowerTransactionAddedNotification extends Notification {
	private static final long serialVersionUID = 1L;

	private TransactionEntry transactionEntry;
	
	@Override
	@Transient
	public NotificationType getType() {
		return NotificationType.FOLLOWER_TRANSACTION_ADDED;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public TransactionEntry getTransactionEntry() {
		return transactionEntry;
	}

	public void setTransactionEntry(TransactionEntry transactionEntry) {
		this.transactionEntry = transactionEntry;
	}
	
}
