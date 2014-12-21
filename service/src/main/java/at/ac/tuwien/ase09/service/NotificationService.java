package at.ac.tuwien.ase09.service;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.notification.Notification;

@Stateless
public class NotificationService {
	@Inject
	private EntityManager em;
	
	public void addNotification(Notification n){
		em.persist(n);
	}

}
