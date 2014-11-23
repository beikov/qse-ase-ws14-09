package at.ac.tuwien.ase09.model;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class NewsItem extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;
	
	private String title;
	private String text;
	private String source;
	private Calendar created;
	private ValuePaper valuePaper;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
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
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public ValuePaper getValuePaper() {
		return valuePaper;
	}
	
	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	
}