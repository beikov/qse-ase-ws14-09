package at.ac.tuwien.ase09.model.order;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.Money;

@Entity
@DiscriminatorValue(OrderType.TYPE_LIMIT)
public class LimitOrder extends Order {
	private static final long serialVersionUID = 1L;

	private Money limit;
	private Money stopLimit;
	
	@Override
	@Transient
	public OrderType getType() {
		return OrderType.LIMIT;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "currency", column = @Column(name="limit_currency")),
		@AttributeOverride(name = "value", column = @Column(name="limit_value"))
	})
	public Money getLimit() {
		return limit;
	}

	public void setLimit(Money limit) {
		this.limit= limit;
	}
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "currency", column = @Column(name="stopLimit_currency")),
		@AttributeOverride(name = "value", column = @Column(name="stopLimit_value"))
	})
	public Money getStopLimit() {
		return stopLimit;
	}

	public void setStopLimit(Money stopLimit) {
		this.stopLimit = stopLimit;
	}
	
}
