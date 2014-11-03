package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;

@Entity
@DiscriminatorValue(NotificationType.TYPE_FOLLOWER_ADDED)
public class FollowerAddedNotification extends Notification {

	private User follower;
	
	@Override
	public NotificationType getType() {
		return NotificationType.FOLLOWER_ADDED;
	}
}
