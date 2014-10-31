package at.ac.tuwien.ase09.context;

import javax.enterprise.context.RequestScoped;

import at.ac.tuwien.ase09.model.User;

@RequestScoped
public class WebUserContext implements UserContext {

	private static final long serialVersionUID = 1L;

	@Override
	public User getUser() {
		// TODO Auto-generated method stub
		return null;
	}

}
