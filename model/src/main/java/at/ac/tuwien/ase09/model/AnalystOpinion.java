package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Entity;

@Entity
public class AnalystOpinion extends BaseEntity<Long> {

	private String text;
	private String source;
	private Calendar created;
	private AnalystRecommendation recommendation;
	private Money targetPrice;
	
}
