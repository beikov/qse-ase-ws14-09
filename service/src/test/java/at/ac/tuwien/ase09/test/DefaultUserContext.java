package at.ac.tuwien.ase09.test;

import javax.enterprise.context.RequestScoped;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@RequestScoped
public class DefaultUserContext implements UserContext {

	private static final long serialVersionUID = 1L;

	private User user;
	private Portfolio context;

	@Override
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public Portfolio getContext() {
		return context;
	}

	public void setContext(Portfolio context) {
		this.context = context;
	}	
}
