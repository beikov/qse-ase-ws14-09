package at.ac.tuwien.ase09.test;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.Login;
import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.keycloak.UserInfo;
import at.ac.tuwien.ase09.model.User;

@SessionScoped
public class TestUserContext implements UserContext {
private static final long serialVersionUID = 1L;
	
	private UserInfo userInfo;
	
	void init(@Observes @Login UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	@Produces
	@Named
	@SessionScoped
	@Override
	public User getUser() {
		if (userInfo == null) {
			return new User("Gast");
		}
		return userInfo.getUser();
	}

	@Override
	public Long getContextId() {
		if (userInfo == null) {
			return null;
		}

		return userInfo.getContextId();
	}

}
