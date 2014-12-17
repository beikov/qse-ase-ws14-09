package at.ac.tuwien.ase09.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;

@Stateless
public class NotificationService {

	public List<? extends Notification> getNotificationsForUser(User u){
		List<Notification> notifications = new ArrayList<Notification>(); 
		
		User us = new User();
		us.setUsername("Herbert");
		FollowerAddedNotification f1 = new FollowerAddedNotification();
		f1.setCreated(Calendar.getInstance());
		f1.setFollower(us);
		f1.setCreated(Calendar.getInstance());
		notifications.add(f1);
		
		StockMarketGame s1 = new StockMarketGame();
		s1.setName("Spiel1");
		GameStartedNotification g1 = new GameStartedNotification();
		g1.setGame(s1);
		g1.setCreated(Calendar.getInstance());
		notifications.add(g1);
		
		return notifications;
	}
	
}
