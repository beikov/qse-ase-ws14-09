package at.ac.tuwien.ase09.model.order;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue(OrderType.TYPE_MARKET)
public class MarketOrder extends Order {
	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public OrderType getType() {
		return OrderType.MARKET;
	}
	
}
