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
	public UserAccount getUser() {
		if (userInfo == null) {
			return new UserAccount("Gast");
		}
		UserAccount account = new UserAccount();
		account.setId(userInfo.getUser().getId());
		account.setUsername(userInfo.getUser().getUsername());
		account.setFirstName(userInfo.getFirstName());
		account.setLastName(userInfo.getLastName());
		account.setEmail(userInfo.getEmail());
		account.setLogo(userInfo.getUser().getLogo());
		return account;
	}
	
	@Override
	public Long getUserId() {
		if (userInfo == null) {
			return null;
		}
		
		return userInfo.getUser().getId();
	}

	@Override
	public Long getContextId() {
		if (userInfo == null) {
			return null;
		}

		return userInfo.getContextId();
	}

}
