package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.UserService;

@Named
@ViewScoped
public class UserProfileBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserDataAccess userDataAccess;
	
	@Inject
	private WebUserContext userContext;
	
	@Inject
	private UserService userService;
	
	private String username;
	private User owner;
	private User user;
	private List<User> followers;
	private List<Portfolio> portfolios;
	
	 //private DashboardModel portfolioDashboard;
	
	
	public void init() throws IOException {
		try {
			owner = userDataAccess.loadUserForProfile(username);
		} catch(EntityNotFoundException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(404, "Der Benutzer '" + username + "' wurde nicht gefunden");
			context.responseComplete();
			return;
		} catch(AppException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(500, "Fehler beim Laden des Benutzerprofils");
			context.responseComplete();
			return;
		}
		user = userContext.getUser();
	
		followers = new ArrayList<>(owner.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(owner);
		//createPortfolioDashboard();
	}
	
	public void validateUsername() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (!context.isPostback() && context.isValidationFailed()) {
			context.getExternalContext().responseSendError(500, "Fehlerhafter Benutzername");
			context.responseComplete();
		}
	}
	
	/*private void createPortfolioDashboard() {
		portfolioDashboard = new DefaultDashboardModel();
		
		DashboardColumn column1 = new DefaultDashboardColumn();
		DashboardColumn column2 = new DefaultDashboardColumn();
		DashboardColumn column3 = new DefaultDashboardColumn();
		int i = 0;
		for (Portfolio p : portfolios) {
			DashboardColumn column = new DefaultDashboardColumn();
			column.addWidget(p.getName());
			switch(i % 3) {
			case 0:
				column1.addWidget(p.getName());
				break;
			case 1:
				column2.addWidget(p.getName());
				break;
			case 2:
				column3.addWidget(p.getName());
				break;
			}
			i++;
		}
 
        portfolioDashboard.addColumn(column1);
        portfolioDashboard.addColumn(column2);
        portfolioDashboard.addColumn(column3);
	}*/
	
	public String getFollowerName(User follower) {
		String currentUsername = userContext.getUser().getUsername();
		String followerUsername = follower.getUsername();
		
		if (followerUsername.equals(currentUsername))
			return "Ich";
		return followerUsername;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public User getUser() {
		return user;
	}
	
	public boolean isProfileOwner() {
		return user.getId() == owner.getId(); 
	}
	
	public boolean isFollowable(){
		return (!user.getUsername().equals("Gast") && !owner.getFollowers().contains(user) && !isProfileOwner());
	}
	
	public boolean isUnfollowable(){
		return (owner.getFollowers().contains(user));
	}
	
	public List<User> getFollowers() {
		return followers;
	}
	
	public List<Portfolio> getPortfolios() {
		return portfolios;
	}
	
	public void follow(){
		userService.followUser(owner, user);
	}
	
	public void unfollow(){
		System.out.println("unfollowing");
		userService.unfollowUser(owner,user);
	}
	
	/*public DashboardModel getPortfolioDashboard() {
		return portfolioDashboard;
	}*/
}
