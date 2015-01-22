package at.ac.tuwien.ase09.model.order;

import java.math.BigDecimal;

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

	private BigDecimal limit;
	private BigDecimal stopLimit;
	
	@Override
	@Transient
	public OrderType getType() {
		return OrderType.LIMIT;
	}

	public BigDecimal getLimit() {
		return limit;
	}

	public void setLimit(BigDecimal limit) {
		this.limit= limit;
	}
	
	public BigDecimal getStopLimit() {
		return stopLimit;
	}

	public void setStopLimit(BigDecimal stopLimit) {
		this.stopLimit = stopLimit;
	}
	
}
