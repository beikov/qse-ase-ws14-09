package at.ac.tuwien.ase09.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.service.NotificationService;

@Named
@SessionScoped
public class NotificationBean {

	private List<? extends Notification> notifications;

	@Inject
	private NotificationDataAccess data;

	@Inject
	private NotificationService service;

	@Inject
	WebUserContext userContext;


	@PostConstruct
	public void init(){
		notifications = data.getNotificationsForUser(userContext.getUser());
	}
	

	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<? extends Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<? extends Notification> notifications) {
		this.notifications = notifications;
	}


	public String getTextForNotification(FollowerAddedNotification notification) {
		notification.setRead(true);
		service.setRead(notification);
		return "Benutzer: '"+notification.getFollower().getUsername()+"' folgt Ihnen nun.";
	}

	public String getTextForNotification(GameStartedNotification notification) {
		return "Das Spiel: '"+notification.getGame().getName()+"' hat begonnen.";
	}

	public int getUnreadCount(){
		return data.getUnreadNotificationsCount(userContext.getUser());
	}


}
