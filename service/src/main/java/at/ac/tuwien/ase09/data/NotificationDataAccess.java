package at.ac.tuwien.ase09.data;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Hibernate;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.FollowerTransactionAddedNotification;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.model.notification.PortfolioFollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;

@Stateless
public class NotificationDataAccess {

	@Inject
	private EntityManager em;

	public List<? extends Notification> getNotificationsForUser(Long userId){
		try{
			List<Notification> ret = em.createQuery("FROM Notification n WHERE n.user.id = :userId ORDER BY n.created DESC", Notification.class).setParameter("userId", userId).getResultList();
		
			for (Notification notification : ret) {
				switch (notification.getType()) {
				case FOLLOWER_ADDED: 
					Hibernate.initialize(((FollowerAddedNotification)notification).getFollower());
					break;
				case FOLLOWER_TRANSACTION_ADDED:
					Hibernate.initialize(((FollowerTransactionAddedNotification)notification).getTransactionEntry());
					break;
				case GAME_STARTED:
					Hibernate.initialize(((GameStartedNotification)notification).getGame());
					break;
				case WATCH_TRIGGERED: 
					Hibernate.initialize(((WatchTriggeredNotification)notification).getWatch());
					break;
				case PORTFOLIO_FOLLOWER_ADDED:
					Hibernate.initialize(((PortfolioFollowerAddedNotification)notification).getPortfolio());
					Hibernate.initialize(((PortfolioFollowerAddedNotification)notification).getFollower());
					break;
				}
				Hibernate.initialize(notification);
				Hibernate.initialize(notification.getCreated());
				Hibernate.initialize(notification.getUser());
			}

			return ret;
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<? extends Notification> getUnreadNotificationsForUser(Long userId){
		try{
			List<Notification> ret = em.createQuery("FROM Notification n WHERE n.user.id = :userId AND n.read = false  ORDER BY n.created DESC", Notification.class).setParameter("userId", userId).getResultList();
			
			for (Notification notification : ret) {
				switch (notification.getType()) {
				case FOLLOWER_ADDED: 
					Hibernate.initialize(((FollowerAddedNotification)notification).getFollower());
					break;
				case FOLLOWER_TRANSACTION_ADDED:
					Hibernate.initialize(((FollowerTransactionAddedNotification)notification).getTransactionEntry());
					break;
				case GAME_STARTED:
					Hibernate.initialize(((GameStartedNotification)notification).getGame());
					break;
				case WATCH_TRIGGERED: 
					Hibernate.initialize(((WatchTriggeredNotification)notification).getWatch());
					break;
				case PORTFOLIO_FOLLOWER_ADDED:
					Hibernate.initialize(((PortfolioFollowerAddedNotification)notification).getPortfolio());
					Hibernate.initialize(((PortfolioFollowerAddedNotification)notification).getFollower());
					break;
				}
				Hibernate.initialize(notification);
				Hibernate.initialize(notification.getCreated());
				Hibernate.initialize(notification.getUser());
			}

			return ret;
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public int getUnreadNotificationsCount(Long userId) {
		try{
			return  em.createQuery("SELECT count(n) FROM Notification n WHERE n.user.id = :userId AND n.read = false", Long.class).setParameter("userId", userId).getSingleResult().intValue();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<Notification> getUnpushedNotifications(Long userId) {
		try{
			List<Notification> ret = em.createQuery("FROM Notification n WHERE n.user.id = :userId AND n.pushed = false ORDER BY n.created DESC", Notification.class).setParameter("userId", userId).getResultList();
			em.createQuery("UPDATE Notification n SET n.pushed = true WHERE n.user.id = :userId AND n.pushed = false").setParameter("userId", userId).executeUpdate();	

			initialize(ret);

			return ret;
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	private void initialize(List<Notification> ret) {
		for (Notification notification : ret) {
			switch (notification.getType()) {
			case FOLLOWER_ADDED: 
				Hibernate.initialize(((FollowerAddedNotification)notification).getFollower());
				break;
			case FOLLOWER_TRANSACTION_ADDED:
				Hibernate.initialize(((FollowerTransactionAddedNotification)notification).getTransactionEntry());
				break;
			case GAME_STARTED:
				Hibernate.initialize(((GameStartedNotification)notification).getGame());
				break;
			case WATCH_TRIGGERED: 
				Hibernate.initialize(((WatchTriggeredNotification)notification).getWatch());
				break;
			case PORTFOLIO_FOLLOWER_ADDED:
				Hibernate.initialize(((PortfolioFollowerAddedNotification)notification).getPortfolio());
				Hibernate.initialize(((PortfolioFollowerAddedNotification)notification).getFollower());
				break;
			}
			Hibernate.initialize(notification);
			Hibernate.initialize(notification.getCreated());
			Hibernate.initialize(notification.getUser());
		}
	}

}
