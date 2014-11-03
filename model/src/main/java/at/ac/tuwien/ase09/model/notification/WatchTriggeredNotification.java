package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import at.ac.tuwien.ase09.model.Watch;

@Entity
@DiscriminatorValue(NotificationType.TYPE_WATCH_TRIGGERED)
public class WatchTriggeredNotification extends Notification {

	private Watch watch;
	
	@Override
	public NotificationType getType() {
		return NotificationType.WATCH_TRIGGERED;
	}
}
