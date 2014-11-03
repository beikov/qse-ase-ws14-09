package at.ac.tuwien.ase09.model;

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

@Entity
public class Institution extends BaseEntity<Long> {

	private String name;
	private Blob logo;
	private User admin;
	private String pageText;
}
