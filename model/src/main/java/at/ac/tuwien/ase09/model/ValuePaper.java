package at.ac.tuwien.ase09.model;

import java.util.Currency;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class ValuePaper extends BaseEntity<Long> {

	private String name;
	private Currency currency;
	
}
