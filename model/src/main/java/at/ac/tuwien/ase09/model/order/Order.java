package at.ac.tuwien.ase09.model.order;

import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaper;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ORDER_TYPE")
public abstract class Order extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;
	
	private Calendar created;
	private OrderAction orderAction;
	private Calendar validFrom;
	private Calendar validTo;
	private Portfolio portfolio;
	private Integer volume;
	private OrderStatus status;
	private ValuePaper valuePaper;

	@Transient
	public abstract OrderType getType();
	
	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	//@Enumerated(EnumType.STRING)
	public OrderAction getOrderAction() {
		return orderAction;
	}

	public void setOrderAction(OrderAction orderAction) {
		this.orderAction = orderAction;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

	@Temporal(TemporalType.DATE)
	public Calendar getValidTo() {
		return validTo;
	}

	public void setValidTo(Calendar validTo) {
		this.validTo = validTo;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public ValuePaper getValuePaper() {
		return valuePaper;
	}

	public void setValuePaper(ValuePaper valuePaper) {
		this.valuePaper = valuePaper;
	}
	
}
