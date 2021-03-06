package at.ac.tuwien.ase09.model;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Institution extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private String name;
	private User admin;
	private String pageText;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	public User getAdmin() {
		return admin;
	}
	
	public void setAdmin(User admin) {
		this.admin = admin;
	}
	
	@Lob
	public String getPageText() {
		return pageText;
	}
	
	public void setPageText(String pageText) {
		this.pageText = pageText;
	}

}
