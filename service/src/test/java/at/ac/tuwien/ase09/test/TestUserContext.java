package at.ac.tuwien.ase09.test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.model.User;

@Alternative
@RequestScoped
public class TestUserContext implements UserContext {

	private static final long serialVersionUID = 1L;

	private User user;

	@Override
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}	
}