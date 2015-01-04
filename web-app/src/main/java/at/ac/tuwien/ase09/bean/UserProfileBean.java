package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Named
@ViewScoped
public class UserProfileBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserDataAccess userDataAccess;
	
	@Inject
	private UserContext userContext;
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	
	private String username;
	private User owner;
	private User user;
	private boolean institutionAdmin;
	private Institution institution;
	private List<User> followers;
	private List<Portfolio> portfolios;
	
	 private DashboardModel portfolioDashboard;
	
	
	public void init() {
		if (username == null) {
			// no param given
			return;
		}

		owner = userDataAccess.loadUserForProfile(username);
		user = userContext.getUser();
		
		institution = institutionDataAccess.getByAdmin(owner.getUsername());
		if (institution != null) {
			institutionAdmin = true;
		}
		
		followers = new ArrayList<>(owner.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(owner);
		createPortfolioDashboard();
	}
	
	private void createPortfolioDashboard() {
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
	
	public boolean isInstitutionAdmin() {
		return institutionAdmin;
	}
	
	public Institution getInstitution() {
		return institution;
	}
	
	public List<User> getFollowers() {
		return followers;
	}
	
	public List<Portfolio> getPortfolios() {
		return portfolios;
	}
	
	public DashboardModel getPortfolioDashboard() {
		return portfolioDashboard;
	}
}
