package at.ac.tuwien.ase09.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
public class User extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
