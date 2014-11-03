package at.ac.tuwien.ase09.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

@Entity
public class User extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	private String username;
	// email? country?
	private Set<User> followers = new HashSet<>();

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
