package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.Watch;

@Entity
@DiscriminatorValue(NotificationType.TYPE_WATCH_TRIGGERED)
public class WatchTriggeredNotification extends Notification {
	private static final long serialVersionUID = 1L;

	private Watch watch;
	
	@Override
	@Transient
	public NotificationType getType() {
		return NotificationType.WATCH_TRIGGERED;
	}
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Watch getWatch() {
		return watch;
	}

	public void setWatch(Watch watch) {
		this.watch = watch;
	}
	
}
