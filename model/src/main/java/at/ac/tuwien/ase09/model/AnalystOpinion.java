package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
	private Stock stock;

	@Lob
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

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}
	
}
