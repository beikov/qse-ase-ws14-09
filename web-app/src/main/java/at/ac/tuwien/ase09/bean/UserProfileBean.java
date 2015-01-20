package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.InstitutionService;
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
	private UserService userService;
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	@Inject
	private InstitutionService institutionService;
	
	@Inject
	private WebUserContext userContext;
	
	private String username;
	private User owner;
	private User user;
	private Institution institution;
	private List<User> followers;
	private List<Portfolio> portfolios;
	
	public void init() throws IOException {
		if (username == null) {
			//profileSettings without viewParam
			username = userContext.getUser().getUsername();
		}
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
		try {
			institution = institutionDataAccess.getByAdmin(username);
			// institution != null -> owner == institutionAdmin
		} catch(EntityNotFoundException e) {
		} catch(AppException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(500, "Fehler beim Laden der Institution");
			context.responseComplete();
			return;
		}
		
		user = userContext.getUser();
	
		followers = new ArrayList<>(owner.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(owner);
		//createPortfolioDashboard();
	}
	
	public void validateUsername() throws IOException {
		System.out.println("username validate " + username);
		FacesContext context = FacesContext.getCurrentInstance();
		if (!context.isPostback() && context.isValidationFailed()) {
			context.getExternalContext().responseSendError(500, "Fehlerhafter Benutzername");
			context.responseComplete();
		}
	}

	public void saveChanges() {
		try {
			userService.updateUser(user);
			if (institution != null)
				institutionService.update(institution);
			FacesMessage message = new FacesMessage("Änderungen erfolgreich gespeichert");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage("Fehler beim Speichern der Änderungen");
	        FacesContext.getCurrentInstance().addMessage(null, message);
	        e.printStackTrace();
		}
	}
	
	public String getFollowerName(User follower) {
		String currentUsername = userContext.getUser().getUsername();
		String followerUsername = follower.getUsername();
		
		try {
			Institution institution = institutionDataAccess.getByAdmin(followerUsername);
			if (currentUsername.equals(institution.getAdmin().getUsername()))
				return "Eigene Institution";
			return institution.getName();
		} catch(EntityNotFoundException e) {}
		
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
		return user.getUsername().equals(owner.getUsername()); 
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
	
	public Institution getInstitution() {
		return institution;
	}

	public void follow(){
		userService.followUser(owner, user);
	}
	
	public void unfollow(){
		System.out.println("unfollowing");
		userService.unfollowUser(owner,user);
	}
	
}
