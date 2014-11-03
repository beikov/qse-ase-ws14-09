package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Entity;

@Entity
public class Watch extends BaseEntity<Long> {

	private String expression;
	private ValuePaper valuePaper;
	private User owner;
	private Calendar created;
}
