package at.ac.tuwien.ase09.test;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.UserContext;

@Stateless
public class TestUserContext implements UserContext {
	private static final long serialVersionUID = 1L;

	@Override
	public UserAccount getUser() {
		return null;
	}

	@Override
	public Long getUserId() {
		return null;
	}

	@Override
	public Long getContextId() {
		return null;
	}

}
