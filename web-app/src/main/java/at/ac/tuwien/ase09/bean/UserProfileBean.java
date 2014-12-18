package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Named
@javax.faces.view.ViewScoped
public class UserProfileBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserDataAccess userDataAccess;
	
	@Inject
	private WebUserContext userContext;
	
	private String ownerName;
	private User owner;
	private User user;
	private List<User> followers;
	private List<Portfolio> portfolios;
	
	public void init() {
		owner = userDataAccess.loadUserForProfile(ownerName);
		followers = new ArrayList<>(owner.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(owner);
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public List<User> getFollowers() {
		return followers;
	}
	
	public List<Portfolio> getPortfolios() {
		return portfolios;
	}
}
