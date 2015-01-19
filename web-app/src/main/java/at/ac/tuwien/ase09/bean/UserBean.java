package at.ac.tuwien.ase09.bean;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import at.ac.tuwien.ase09.keycloak.AdminClient;

@Named
@RequestScoped
public class UserBean {

	@Inject
	private HttpServletRequest request;
	
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
}
