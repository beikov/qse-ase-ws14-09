package at.ac.tuwien.ase09.model.transaction;

import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Check;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.order.OrderType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TRANSACTION_TYPE")
public abstract class TransactionEntry extends BaseEntity<Long> {
	private static final long serialVersionUID = 1L;

	private Portfolio portfolio;
	private Money value;
	private Calendar created;

	@Transient
	public abstract TransactionType getType();

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	/**
	 * 
	 * @return value that is negative if value was deducted from the user's account and positive if value was added to the user's account
	 */
	@Embedded
	public Money getValue() {
		return value;
	}

	public void setValue(Money value) {
		this.value = value;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}
	
	@PrePersist
	private void onPrePersist(){
		created = Calendar.getInstance();
	}
}