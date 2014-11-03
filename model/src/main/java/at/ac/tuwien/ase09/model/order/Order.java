package at.ac.tuwien.ase09.model.order;

import java.util.Calendar;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.BaseEntity;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaper;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ORDER_TYPE")
public abstract class Order extends BaseEntity<Long> {

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
}
