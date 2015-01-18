package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Watch extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private String expression;
	private ValuePaper valuePaper;
	private User owner;
	private Calendar created;

	@PrePersist
	protected void onCreate() {
		if (created == null) {
			created = Calendar.getInstance();
		}
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public ValuePaper getValuePaper() {
		return valuePaper;
	}
	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}
	public void setCreated(Calendar created) {
		this.created = created;
	}

}
