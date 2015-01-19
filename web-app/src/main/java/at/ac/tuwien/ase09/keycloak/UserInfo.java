package at.ac.tuwien.ase09.keycloak;

import java.io.Serializable;

import at.ac.tuwien.ase09.model.User;

public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private User user;
	private Long contextId;
	private final String username;
	private final String firstName;
	private final String lastName;
	private final String email;
	
	public UserInfo() {
		this.username = null;
		this.firstName = null;
		this.lastName = null;
		this.email = null;
	}

	public UserInfo(String username, String firstName, String lastName, String email) {
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}
}
