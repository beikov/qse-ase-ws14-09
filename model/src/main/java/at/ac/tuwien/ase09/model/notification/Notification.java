package at.ac.tuwien.ase09.model.notification;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.User;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "NOTIFICATION_TYPE")
public abstract class Notification extends BaseEntity<Long> {

	private User user;
	private Calendar created;
	private Boolean read = false;
	private Boolean pushed = false;

	public abstract NotificationType getType();
	
}
