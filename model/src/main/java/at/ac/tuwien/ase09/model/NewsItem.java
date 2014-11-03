package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Entity;

@Entity
public class NewsItem extends BaseEntity<Long> {

	private String title;
	private String text;
	private String source;
	private Calendar created;
	private ValuePaper valuePaper;
	
}
