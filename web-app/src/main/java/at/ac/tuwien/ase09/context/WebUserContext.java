package at.ac.tuwien.ase09.context;

import javax.enterprise.context.RequestScoped;
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
