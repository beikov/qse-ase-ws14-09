package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.UserService;

@Named
@ViewScoped
public class UserProfileSettingsBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserDataAccess userDataAccess;
	
	@Inject
	private UserService userService;
	
	@Inject
	private WebUserContext userContext;
	
	private User user;
	private List<User> followers;
	private List<Portfolio> portfolios;
	
	@PostConstruct
	public void init() {
		user = userDataAccess.loadUserForProfile(userContext.getUser().getUsername());
		
		followers = new ArrayList<>(user.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(userContext.getUserId());
	}
	
	public User getUser() {
		return user;
	}
	
	public List<User> getFollowers() {
		return followers;
	}
	
	public List<Portfolio> getPortfolios() {
		return portfolios;
	}
	
	
	public void saveChanges() {
		try {
			userService.updateUser(user);
			
			FacesMessage message = new FacesMessage("Änderungen erfolgreich gespeichert");
	        FacesContext.getCurrentInstance().addMessage(null, message);
		} catch (Exception e) {
			FacesMessage message = new FacesMessage("Fehler beim Speichern der Änderungen");
	        FacesContext.getCurrentInstance().addMessage(null, message);
	        e.printStackTrace();
		}
	}
}
