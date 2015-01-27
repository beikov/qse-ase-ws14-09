package at.ac.tuwien.ase09.model.order;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

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

	@NotNull
	@Basic(optional = false)
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
