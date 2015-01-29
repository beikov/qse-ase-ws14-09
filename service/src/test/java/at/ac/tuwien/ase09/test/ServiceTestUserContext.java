package at.ac.tuwien.ase09.test;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.UserContext;

@Stateless
public class ServiceTestUserContext implements UserContext {
	private static final long serialVersionUID = 1L;

	private Long userId;
	private Long contextId;
	
	@Override
	public UserAccount getUser() {
		return null;
	}

	@Override
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	@Override
	public Long getContextId() {
		return contextId;
	}

}
