package at.ac.tuwien.ase09.model;

import javax.persistence.Entity;

@Entity
public class Stock extends ValuePaper {
	private String certificatePageLink;
	private String isin;
}
