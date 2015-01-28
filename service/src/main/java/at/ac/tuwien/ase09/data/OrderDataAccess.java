package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderStatus;

@Stateless
public class OrderDataAccess extends AbstractDataAccess {

	public List<Order> getActiveOrders() {
		try {
			return em.createQuery("FROM Order o WHERE o.status = :openState AND CURRENT_TIMESTAMP BETWEEN o.validFrom AND o.validTo", Order.class)
					.setParameter("openState", OrderStatus.OPEN)
					.getResultList();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
