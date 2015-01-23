package at.ac.tuwien.ase09.model;

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import at.ac.tuwien.ase09.naming.CustomNamingStrategy;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_username", columnNames = "c_username"))
public class User extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	private String username;
	private Blob logo;
	// email? country?
	
	private Set<User> followers = new HashSet<>();

	public User() {
		super();
	}

	public User(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Blob getLogo() {
		return logo;
	}

	public void setLogo(Blob logo) {
		this.logo = logo;
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
