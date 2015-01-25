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
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;
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
	private StockMarketGameDataAccess gameDataAccess;
	
	@Inject
	private WebUserContext userContext;
	
	private String username;
	private User owner;
	private User user;
	private boolean isOwner;
	private Institution institution;
	private List<StockMarketGame> institutionGames = new ArrayList<>();
	private List<User> followers = new ArrayList<>();
	private List<Portfolio> portfolios = new ArrayList<>();
	
	public void initProfileView() throws IOException {
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
		loadInstitution(owner, true);
		

		isOwner = owner.getId().equals(userContext.getUserId());
		user = userContext.getUserId() == null ? null : userDataAccess.getUserById(userContext.getUserId());
    	followers = new ArrayList<>(owner.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(owner.getId());
		//createPortfolioDashboard();
	}
	
	public void initProfileSettings() throws IOException {
		//profileSettings without viewParam
		user = userDataAccess.getUserById(userContext.getUserId());
		loadInstitution(user, false);
		 
	}
	
	private void loadInstitution(User user, boolean loadGames) throws IOException {
		try {
			institution = institutionDataAccess.getByAdmin(user.getUsername());
			// institution != null -> owner == institutionAdmin
		} catch(EntityNotFoundException e) {
		} catch(AppException e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(500, "Fehler beim Laden der Institution");
			context.responseComplete();
			return;
		}
		if (loadGames) {
			if (institution == null) {
				return;
			}
			try {
				institutionGames = gameDataAccess.getByInstitutionId(institution.getId());
			} catch(EntityNotFoundException e) {
			} catch(AppException e) {
				FacesMessage message = new FacesMessage("Fehler beim Laden der Börsenspiele");
		        FacesContext.getCurrentInstance().addMessage(null, message);
			}
		}
	}
	
	public void validateUsername() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (!context.isPostback() && context.isValidationFailed()) {
			context.getExternalContext().responseSendError(500, "Fehlerhafter Benutzername");
			context.responseComplete();
		}
	}
	
	public void deleteLogo() {
		try {
			userService.deleteLogo(user);
			user.setLogo(null);
			FacesMessage message = new FacesMessage("Logo erfolgreich gelöscht");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage("Fehler beim Löschen des Logos");
	        FacesContext.getCurrentInstance().addMessage(null, message);
	        e.printStackTrace();
		}
	}

	public void saveChanges() {
		try {
			user = userService.updateUser(user);
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
	
	public boolean allPortfoliosHidden() {
		if (isOwner) {
			return false;
		}
		for (Portfolio p : portfolios) {
			if (p.getVisibility().getPublicVisible()) {
				return false;
			}
		}
		return true;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public User getUser() {
		return user;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public boolean isProfileOwner() {
		return isOwner; 
	}
	
	public boolean isInstitutionAdmin() {
		return institution != null;
	}
    
    public boolean isFollowable(){
		return (userContext.getUserId() != null && !owner.getFollowers().contains(user) && !isProfileOwner());
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
	
	public List<StockMarketGame> getInstitutionGames() {
		return institutionGames;
	}

	public String getFollowUnfollowButtonText() {
		if (isFollowable()) {
			return "Folgen";
		} else if (isUnfollowable()) {
			return "Nicht mehr folgen";
		}
		return "";
	}
	
	public void followUnfollow() {
		if (isFollowable()) {
			owner = userService.followUser(owner, user);
		} else if (isUnfollowable()) {
			owner = userService.unfollowUser(owner,user);
		}
		followers = new ArrayList<>(owner.getFollowers());
	}
	
}
