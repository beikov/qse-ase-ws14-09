package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;

@Entity
@DiscriminatorValue(NotificationType.TYPE_FOLLOWER_TRANSACTION_ADDED)
public class FollowerTransactionAddedNotification extends Notification {

	private TransactionEntry transactionEntry;
	
	@Override
	public NotificationType getType() {
		return NotificationType.FOLLOWER_TRANSACTION_ADDED;
	}
}
