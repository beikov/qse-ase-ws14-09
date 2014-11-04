package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class AnalystOpinion extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private String text;
	private String source;
	private Calendar created;
	private AnalystRecommendation recommendation;
	private Money targetPrice;
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}
	
	public void setCreated(Calendar created) {
		this.created = created;
	}
	
	public AnalystRecommendation getRecommendation() {
		return recommendation;
	}
	
	public void setRecommendation(AnalystRecommendation recommendation) {
		this.recommendation = recommendation;
	}
	
	@Embedded
	public Money getTargetPrice() {
		return targetPrice;
	}
	
	public void setTargetPrice(Money targetPrice) {
		this.targetPrice = targetPrice;
	}
	
}
