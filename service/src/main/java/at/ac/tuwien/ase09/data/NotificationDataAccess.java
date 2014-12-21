package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.Notification;

public class NotificationDataAccess {

	@Inject
	private EntityManager em;

	public List<? extends Notification> getNotificationsForUser(User u){
		try{
			return em.createQuery("FROM Notification n WHERE n.user = :user ORDER BY n.created", Notification.class).setParameter("user", u).getResultList();
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

}
