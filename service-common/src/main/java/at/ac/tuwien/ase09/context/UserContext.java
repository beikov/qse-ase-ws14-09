package at.ac.tuwien.ase09.context;

import java.io.Serializable;

public interface UserContext extends Serializable {

	public UserAccount getUser();

	public Long getUserId();
	
	public Long getContextId();
}
