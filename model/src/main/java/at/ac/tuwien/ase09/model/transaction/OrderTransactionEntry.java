package at.ac.tuwien.ase09.model.transaction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import at.ac.tuwien.ase09.model.order.Order;

/**
 * Durchführung der Order, Verkauf erzeugt TaxTransactionEntry für KeSt
 * 
 * @author Christian
 *
 */
@Entity
@DiscriminatorValue(TransactionType.TYPE_ORDER)
public class OrderTransactionEntry extends TransactionEntry {
	private static final long serialVersionUID = 1L;

	private Order order;
	
	@Override
	@Transient
	public TransactionType getType() {
		return TransactionType.ORDER;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
}
