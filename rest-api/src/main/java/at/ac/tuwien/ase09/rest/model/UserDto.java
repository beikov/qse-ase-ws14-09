package at.ac.tuwien.ase09.rest.model;

import java.io.Serializable;

public class UserDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;

	public UserDto(long id) {
		super();
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
