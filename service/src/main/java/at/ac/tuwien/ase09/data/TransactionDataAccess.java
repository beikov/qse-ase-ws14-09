package at.ac.tuwien.ase09.data;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;

@Stateless
public class TransactionDataAccess {

	@PersistenceContext
	private EntityManager em;

	public List<OrderTransactionEntry> getBuyTransactions(Portfolio portfolio, ValuePaper valuePaper) {
		try {
			 return em.createQuery("SELECT ot FROM TransactionEntry t, OrderTransactionEntry ot "
			 		+ "LEFT JOIN FETCH ot.order o "
			 		+ "WHERE o.orderAction = 0 AND o.portfolio = :portfolio AND o.valuePaper = :valuePaper", OrderTransactionEntry.class).setParameter("portfolio", portfolio).setParameter("valuePaper", valuePaper).getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public List<OrderTransactionEntry> getBuyTransactionsUntil(Portfolio portfolio, ValuePaper valuePaper, Calendar date) {
		try {
			 return em.createQuery("SELECT ot FROM TransactionEntry t, OrderTransactionEntry ot "
			 		+ "LEFT JOIN FETCH ot.order o "
			 		+ "WHERE ot.id = t.id AND o.orderAction = 0 AND o.portfolio = :portfolio AND o.valuePaper = :valuePaper AND t.created <= :date", OrderTransactionEntry.class).setParameter("portfolio", portfolio).setParameter("valuePaper", valuePaper).setParameter("date", date).getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public BigDecimal getValuePaperBuyPrice(Portfolio portfolio, ValuePaper valuePaper) {
		try {
			 TransactionEntry t = em.createQuery("SELECT t FROM TransactionEntry t, OrderTransactionEntry ot, Order o, Portfolio p, ValuePaper vp "
			 		+ "WHERE t.id = ot.id AND ot.order = o.id AND o.portfolio = :portfolio and vp = :valuePaper", TransactionEntry.class).setParameter("portfolio", portfolio).setParameter("valuePaper", valuePaper).getSingleResult();
			 return t.getValue().getValue();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
}
