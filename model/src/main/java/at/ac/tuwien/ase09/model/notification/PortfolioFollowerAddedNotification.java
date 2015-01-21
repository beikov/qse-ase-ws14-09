package at.ac.tuwien.ase09.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Entity
@DiscriminatorValue(NotificationType.TYPE_PORTFOLIO_FOLLOWER_ADDED)
public class PortfolioFollowerAddedNotification extends Notification {
	private static final long serialVersionUID = 1L;

	private User follower;
	
	private Portfolio portfolio;
	
	@Override
	@Transient
	public NotificationType getType() {
		return NotificationType.PORTFOLIO_FOLLOWER_ADDED;
	}
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public User getFollower() {
		return follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	
	
	
}
	
