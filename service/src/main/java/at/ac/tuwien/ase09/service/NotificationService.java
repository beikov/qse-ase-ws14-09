package at.ac.tuwien.ase09.service;


import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.model.notification.NotificationType;
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;

@Stateless
public class NotificationService {
	@Inject
	private EntityManager em;
	
	@Inject 
	private NotificationDataAccess data;
	
	public void addNotification(Notification n){
		if(n.getType() == NotificationType.WATCH_TRIGGERED
				&& data.existsNotificationForWatch(((WatchTriggeredNotification)n).getWatch())){
			return;
		}
		
		em.persist(n);
	}
	
	public void setRead(Notification n){
		n.setRead(true);
		n.setPushed(true);
		em.merge(n);
	}
	
	public void setPushed(Notification n){
		n.setPushed(true);
		em.merge(n);
	}

	
	public void addGame(StockMarketGame ret) {
		
	}

}
