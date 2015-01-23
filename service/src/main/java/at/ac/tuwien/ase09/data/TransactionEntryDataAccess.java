package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;

@Stateless
public class TransactionEntryDataAccess {

	@Inject
	private EntityManager em;

	public List<OrderTransactionEntry> getOrderTransactionsForPortfolioValuePaper(PortfolioValuePaper pvp) {
		try {
			return em.createQuery("FROM OrderTransactionEntry ot WHERE ot.order.portfolio = :p AND ot.order.valuePaper = :vp  and ot.order.portfolio.deleted=false", OrderTransactionEntry.class).setParameter("p", pvp.getPortfolio()).setParameter("vp", pvp.getValuePaper()).getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
}
