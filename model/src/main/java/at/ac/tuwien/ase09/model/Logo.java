package at.ac.tuwien.ase09.model;

import java.sql.Blob;

import javax.persistence.Transient;


public interface Logo {

	void setLogo(Blob logo);
	Blob getLogo();
	String getFullyQualifiedClassName();
	void setFullyQualifiedClassName(String unused);
}
