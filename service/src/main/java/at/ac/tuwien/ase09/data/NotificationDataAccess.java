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
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;

@Stateless
public class NotificationDataAccess {

	@Inject
	private EntityManager em;

	public List<? extends Notification> getNotificationsForUser(User u){
		try{
			List<Notification> notifications = new ArrayList<Notification>();
			List<FollowerAddedNotification> fan = em.createQuery("FROM FollowerAddedNotification n WHERE n.user = :user ORDER BY n.created", FollowerAddedNotification.class).setParameter("user", u).getResultList();
			List<FollowerTransactionAddedNotification> ftan = em.createQuery("FROM FollowerTransactionAddedNotification n WHERE n.user = :user ORDER BY n.created", FollowerTransactionAddedNotification.class).setParameter("user", u).getResultList();
			List<GameStartedNotification> gsn = em.createQuery("FROM GameStartedNotification n WHERE n.user = :user ORDER BY n.created", GameStartedNotification.class).setParameter("user", u).getResultList();
			List<WatchTriggeredNotification> wtn = em.createQuery("FROM WatchTriggeredNotification n WHERE n.user = :user ORDER BY n.created", WatchTriggeredNotification.class).setParameter("user", u).getResultList();
			for (FollowerAddedNotification n : fan) {
				Hibernate.initialize(n);
				Hibernate.initialize(n.getFollower());
				Hibernate.initialize(n.getUser());
			}
			for (FollowerTransactionAddedNotification n : ftan) {
				Hibernate.initialize(n);
				Hibernate.initialize(n.getTransactionEntry());
				Hibernate.initialize(n.getUser());
			}
			for (GameStartedNotification n : gsn) {
				Hibernate.initialize(n);
				Hibernate.initialize(n.getGame());
				Hibernate.initialize(n.getUser());
			}
			for (WatchTriggeredNotification n : wtn) {
				Hibernate.initialize(n);
				Hibernate.initialize(n.getWatch());
				Hibernate.initialize(n.getUser());
			}
			
			notifications.addAll(fan);
			notifications.addAll(ftan);
			notifications.addAll(gsn);
			notifications.addAll(wtn);
			
			return notifications;
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<? extends Notification> getUnreadNotificationsForUser(User u){
		try{
			return em.createQuery("FROM Notification n WHERE n.user = :user AND n.read = false ORDER BY n.created", Notification.class).setParameter("user", u).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public int getUnreadNotificationsCount(User user) {
		try{
			return  em.createQuery("SELECT count(n) FROM Notification n WHERE n.user = :user AND n.read = false", Long.class).setParameter("user", user).getSingleResult().intValue();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

}
