package at.ac.tuwien.ase09.model;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class ValuePaperPriceEntry extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private ValuePaper valuePaper;
	private Calendar created;
	private BigDecimal price;
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public ValuePaper getValuePaper() {
		return valuePaper;
	}
	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}
	public void setCreated(Calendar created) {
		this.created = created;
	}
	@Column(precision=19, scale=3)
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	@PrePersist
	private void onPrePersist(){
		if(created == null){
			created = Calendar.getInstance();
		}
	}
	
}
