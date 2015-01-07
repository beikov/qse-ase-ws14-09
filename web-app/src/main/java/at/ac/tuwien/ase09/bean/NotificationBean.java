package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.FollowerTransactionAddedNotification;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;
import at.ac.tuwien.ase09.service.NotificationService;

@ManagedBean
@RequestScoped
public class NotificationBean {

	private List<Notification> notifications;
	private boolean reading=false;
	private Notification selectedNotification;

	@Inject
	private NotificationDataAccess data;

	@Inject
	private NotificationService service;

	@Inject
	WebUserContext userContext;


	@PostConstruct
	public void init(){
		notifications = (List<Notification>) data.getNotificationsForUser(userContext.getUser());
	}


	private void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}


	public String getTextForNotification(FollowerAddedNotification notification) {
		updateNotification(notification);
		return "Benutzer: '"+notification.getFollower().getUsername()+"' folgt Ihnen nun.";
	}

	public String getTextForNotification(GameStartedNotification notification) {
		updateNotification(notification);
		return "Das Spiel: '"+notification.getGame().getName()+"' hat begonnen.";
	}

	public String getTextForNotification(FollowerTransactionAddedNotification notification) {
		return "Ein Benutzer dem Sie folgen hat eine Transaktion getätigt";
	}

	public String getTextForNotification(WatchTriggeredNotification notification) {
		return "Eine Ihrer Watches ist getriggert.";
	}

	private void updateNotification(Notification notification){
		if(reading){
			notification.setRead(true);
			service.setRead(notification);
		}
	}

	public int getUnreadCount(){
		return data.getUnreadNotificationsCount(userContext.getUser());
	}

	public void checkNewNotifications(){
		List<Notification> newNot = data.getUnpushedNotifications(userContext.getUser());
		for (Notification notification : newNot) {
			switch (notification.getType()) {
			case FOLLOWER_ADDED: addMessage("Neue Notifikation!", getTextForNotification((FollowerAddedNotification)notification)); break;
			case FOLLOWER_TRANSACTION_ADDED: addMessage("Neue Notifikation!", getTextForNotification((FollowerTransactionAddedNotification)notification)); break;
			case GAME_STARTED: addMessage("Neue Notifikation!", getTextForNotification((GameStartedNotification)notification)); break;
			case WATCH_TRIGGERED: addMessage("Neue Notifikation!", getTextForNotification((WatchTriggeredNotification)notification)); break;
			default: break;
			}
		}
		if(newNot.size() > 0){
			notifications.addAll(newNot);
			RequestContext rContext =  RequestContext.getCurrentInstance();
			rContext.update("notificationComponent:form1:countTxt");
		}
		System.out.println("got new notifications count: "+newNot.size());
	}

	public void read(){
		reading=true;
	}


	public Notification getSelectedNotification() {
		return selectedNotification;
	}


	public void setSelectedNotification(Notification selectedNotification) {
		System.out.println("set selected notification");
		this.selectedNotification = selectedNotification;
	}

	public void onRowSelect(SelectEvent event) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("../"+getNotificationRedirectionUrl(selectedNotification));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getNotificationRedirectionUrl(Notification n){
		switch(n.getType()){
		case FOLLOWER_ADDED: return "user"+((FollowerAddedNotification)n).getFollower().getId()+".xhtml";
		case FOLLOWER_TRANSACTION_ADDED: return "transaction"+((FollowerTransactionAddedNotification)n).getTransactionEntry().getId()+".xhtml"; 
		case GAME_STARTED: return "game"+((GameStartedNotification)n).getGame().getId()+".xhtml"; 
		case WATCH_TRIGGERED: return "watches/watch.xhtml"; 
		default: 
			//this should never happen
			throw new RuntimeException("Unsupported NotificationType");
		}
	}

}


