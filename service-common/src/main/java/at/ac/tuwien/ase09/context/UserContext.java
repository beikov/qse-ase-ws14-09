package at.ac.tuwien.ase09.context;

import java.io.Serializable;

import at.ac.tuwien.ase09.model.User;

public interface UserContext extends Serializable {

	public User getUser();
	
	public Long getContextId();
}
