package at.ac.tuwien.ase09.model.notification;

import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.User;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "NOTIFICATION_TYPE")
public abstract class Notification extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;
	
	private User user;
	private Calendar created;
	private Boolean read = false;
	private Boolean pushed = false;

	@Transient
	public abstract NotificationType getType();

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean getPushed() {
		return pushed;
	}

	public void setPushed(Boolean pushed) {
		this.pushed = pushed;
	}
	
}
