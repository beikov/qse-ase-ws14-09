package at.ac.tuwien.ase09.context;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import at.ac.tuwien.ase09.keycloak.UserInfo;
import at.ac.tuwien.ase09.model.User;

@RequestScoped
public class WebUserContext implements UserContext {

	private static final long serialVersionUID = 1L;
	
	private UserInfo userInfo;
	
	void init(@Observes @Login UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Produces
	@Named
	@RequestScoped
	@Override
	public User getUser() {
		return userInfo.getUser();
	}

}
