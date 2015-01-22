package at.ac.tuwien.ase09.context;

import java.io.Serializable;
import java.sql.Blob;

public class UserAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String username;
	private String email;
	private String firstName;
	private String lastName;
	private Blob logo;

	public UserAccount() {
	}

	public UserAccount(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Blob getLogo() {
		return logo;
	}

	public void setLogo(Blob logo) {
		this.logo = logo;
	}
}
