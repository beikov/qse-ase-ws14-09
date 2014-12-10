package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;

@Stateless
public class TransactionEntryDataAccess {

	@Inject
	private EntityManager em;

	public List<OrderTransactionEntry> getOrderTransactionsForValuePaper(String code) {
		try {
			return em.createQuery("FROM OrderTransactionEntry ot WHERE ot.order.valuePaper.code = :code", OrderTransactionEntry.class).setParameter("code", code).getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
}
