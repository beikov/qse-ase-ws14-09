package at.ac.tuwien.ase09.model.transaction;

import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Check;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.order.OrderType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRANSACTION_TYPE")
@Check(constraints = "C_TRANSACTION_TYPE NOT IN('" + TransactionType.TYPE_ORDER_FEE + "', '" + TransactionType.TYPE_ORDER + "') OR C_ORDER IS NOT NULL")
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
	
	
}
