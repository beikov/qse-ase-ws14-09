package at.ac.tuwien.ase09.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import at.ac.tuwien.ase09.naming.CustomNamingStrategy;

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

	@ManyToMany
	@JoinTable(name="user_user", 
		joinColumns={@JoinColumn(name="user1_id", referencedColumnName=CustomNamingStrategy.COLUMN_PREFIX + "id")},
		inverseJoinColumns={@JoinColumn(name="user2_id", referencedColumnName=CustomNamingStrategy.COLUMN_PREFIX + "id")}
	)
	public Set<User> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<User> followers) {
		this.followers = followers;
	}
	
}
