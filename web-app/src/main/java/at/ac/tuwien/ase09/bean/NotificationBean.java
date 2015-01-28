package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.FollowerTransactionAddedNotification;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.model.notification.NotificationType;
import at.ac.tuwien.ase09.model.notification.PortfolioFollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;
import at.ac.tuwien.ase09.service.NotificationService;

@ManagedBean
@SessionScoped
public class NotificationBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Notification> notifications;
	private boolean showOnlyNew=false;
	private Notification selectedNotification;

	@Inject
	private NotificationDataAccess data;

	@Inject
	private NotificationService service;

	@Inject
	WebUserContext userContext;


	@PostConstruct
	public void init(){
		if(!showOnlyNew){
			notifications = (List<Notification>) data.getNotificationsForUser(userContext.getUserId());
		}else{
			notifications = (List<Notification>) data.getUnreadNotificationsForUser(userContext.getUserId());
		}
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

	public String getTextForNotification(PortfolioFollowerAddedNotification notification) {
		return "Benutzer: '"+notification.getFollower().getUsername()+"' folgt nun Ihrem Portfolio: "+notification.getPortfolio().getName()+".";
	}

	public String getTextForNotification(FollowerAddedNotification notification) {
		return "Benutzer: '"+notification.getFollower().getUsername()+"' folgt Ihnen nun.";
	}

	public String getTextForNotification(GameStartedNotification notification) {
		return "Das Spiel: '"+notification.getGame().getName()+"' hat begonnen.";
	}

	public String getTextForNotification(FollowerTransactionAddedNotification notification) {
		return "Ein Benutzer dem Sie folgen hat eine Transaktion getätigt";
	}

	public String getTextForNotification(WatchTriggeredNotification notification) {
		return "Eine Ihrer Watches ist getriggert.";
	}

	private void updateNotification(Notification notification){
		notification.setRead(true);
		service.setRead(notification);
//		if(showOnlyNew){
//			notifications.remove(notification);
//		}
	}

	public int getUnreadCount(){
		return data.getUnreadNotificationsCount(userContext.getUserId());
	}

	public void checkNewNotifications(){
		List<Notification> newNot = data.getUnpushedNotifications(userContext.getUserId());
		for (Notification notification : newNot) {
			switch (notification.getType()) {
			case FOLLOWER_ADDED: addMessage("Neue Notifikation!", getTextForNotification((FollowerAddedNotification)notification)); break;
			case FOLLOWER_TRANSACTION_ADDED: addMessage("Neue Notifikation!", getTextForNotification((FollowerTransactionAddedNotification)notification)); break;
			case GAME_STARTED: addMessage("Neue Notifikation!", getTextForNotification((GameStartedNotification)notification)); break;
			case WATCH_TRIGGERED: addMessage("Neue Notifikation!", getTextForNotification((WatchTriggeredNotification)notification)); break;
			case PORTFOLIO_FOLLOWER_ADDED: addMessage("Neue Notifikation!", getTextForNotification((PortfolioFollowerAddedNotification)notification)); break;
			default: break;
			}
		}
		if(newNot.size() > 0){
			notifications.addAll(newNot);
			RequestContext rContext =  RequestContext.getCurrentInstance();
			rContext.update("notificationComponent:form1:countTxt");
		}
	}


	public Notification getSelectedNotification() {
		return selectedNotification;
	}


	public void setSelectedNotification(Notification selectedNotification) {
		this.selectedNotification = selectedNotification;
	}

	public void onRowSelect(SelectEvent event) {
		try {
			updateNotification(selectedNotification);
				FacesContext.getCurrentInstance().getExternalContext().redirect("/"+getNotificationRedirectionUrl(selectedNotification));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getNotificationRedirectionUrl(Notification n){
		switch(n.getType()){
		case PORTFOLIO_FOLLOWER_ADDED:
		case FOLLOWER_ADDED: return "user/profile.xhtml?user="+((FollowerAddedNotification)n).getFollower().getUsername();
		case FOLLOWER_TRANSACTION_ADDED: return "transaction"+((FollowerTransactionAddedNotification)n).getTransactionEntry().getId()+".xhtml"; 
		case GAME_STARTED: return "protected/stockmarketgame/stockmarketgameview.xhtml?gameId="+((GameStartedNotification)n).getGame().getId();
		case WATCH_TRIGGERED: return "protected/valuepaper/valuepaperview.xhtml?valuePaperCode="+((WatchTriggeredNotification)n).getWatch().getValuePaper().getCode() ; 
		default: 
			//this should never happen
			throw new RuntimeException("Unsupported NotificationType");
		}
	}

	public void setAllRead(){
		for(Notification notification : notifications){
			updateNotification(notification);
		}
	}


	public boolean isShowOnlyNew() {
		return showOnlyNew;
	}


	public void setShowOnlyNew(boolean showOnlyNew) {
		this.showOnlyNew = showOnlyNew;
		if(!showOnlyNew){
			notifications = (List<Notification>) data.getNotificationsForUser(userContext.getUserId());
		}else{
			notifications = (List<Notification>) data.getUnreadNotificationsForUser(userContext.getUserId());
		}
	}



}


