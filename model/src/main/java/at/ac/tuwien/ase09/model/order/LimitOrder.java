package at.ac.tuwien.ase09.model.order;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.Money;

@Entity
@DiscriminatorValue(OrderType.TYPE_LIMIT)
public class LimitOrder extends Order {

	private Money limitPrice;
	private Money stopLimit;
	
	@Transient
	public OrderType getType() {
		return OrderType.LIMIT;
	}
}
