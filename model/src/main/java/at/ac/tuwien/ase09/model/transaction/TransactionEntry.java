package at.ac.tuwien.ase09.model.transaction;

import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TRANSACTION_TYPE")
public abstract class TransactionEntry extends BaseEntity<Long> {

	private Portfolio portfolio;
	private Money value;
	private Calendar created;

	@Transient
	public abstract TransactionType getType();
}
