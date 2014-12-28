package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.InstitutionService;
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
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	
	@Inject
	private InstitutionService institutionService;
	
	private User user;
	private boolean institutionAdmin;
	private Institution institution;
	private List<User> followers;
	private List<Portfolio> portfolios;
	
	@PostConstruct
	public void init() {
		user = userDataAccess.loadUserForProfile(userContext.getUser().getUsername());
		
		institution = institutionDataAccess.getByAdmin(user.getUsername());
		if (institution != null) {
			institutionAdmin = true;
		}
		
		followers = new ArrayList<>(user.getFollowers());
		portfolios = portfolioDataAccess.getActiveUserPortfolios(user);
	}
	
	public User getUser() {
		return user;
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
	
	
	public void saveChanges() {
		try {
			if (institutionAdmin) {
				institutionService.update(institution);
			}
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
