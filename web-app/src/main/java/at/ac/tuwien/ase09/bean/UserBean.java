package at.ac.tuwien.ase09.bean;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.keycloak.AdminClient;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.User;

@Named
@RequestScoped
public class UserBean {

	@Inject
	private HttpServletRequest request;
	
	@Inject
	private InstitutionDataAccess institutionDataAccess;
	
	@Inject
	private WebUserContext userContext;
	
	public String logout() {
		// Invalidate old session
		request.getSession(true).invalidate();
		// Make sure user gets a new one
		request.getSession(true);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		try {
			facesContext.getExternalContext().redirect(AdminClient.getLogoutUrl(request));
			facesContext.responseComplete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}
	
	public boolean isInstitutionAdmin(UserAccount user) {
		return isInstitutionAdmin(user.getUsername());
	}
	
	public boolean isInstitutionAdmin(User user) {
		return isInstitutionAdmin(user.getUsername());
	}
	
	public boolean isInstitutionAdmin(String username) {
		try {
			institutionDataAccess.getByAdmin(username);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public String getFollowerName(UserAccount follower) {
		return getFollowerName(follower.getUsername());
	}
		
	public String getFollowerName(User follower) {
		return getFollowerName(follower.getUsername());
	}
	
	public String getFollowerName(String followerUsername) {
		String currentUsername = userContext.getUser().getUsername();
		
		try {
			Institution institution = institutionDataAccess.getByAdmin(followerUsername);
			if (institution.getAdmin().getUsername().equals(currentUsername))
				return "Eigene Institution";
			return institution.getName();
		} catch(EntityNotFoundException e) {}
		
		if (followerUsername.equals(currentUsername))
			return "Ich";
		return followerUsername;
	}
}
