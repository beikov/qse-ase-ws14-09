package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.data.TransactionEntryDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.MarketOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class TransactionEntryDataAccessTest extends
		AbstractServiceTest<TransactionEntryDataAccessTest> {
	private static final long serialVersionUID = 1L;

	@Inject
	private TransactionEntryDataAccess transactionEntryDataAccess;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClass(TransactionEntryDataAccess.class);
	}
	
	@Test
	public void testGetOrderTransactionsForValuePaper() {
		// Given
		final String code = "AT123456";
		Stock s = new Stock();
		s.setCode(code);
		
		User user1 = new User();
		User user2 = new User();
		
		Portfolio portfolio1 = new Portfolio();
		portfolio1.setOwner(user1);
		Portfolio portfolio2 = new Portfolio();
		portfolio2.setOwner(user2);
		
		Order marketOrder = new MarketOrder();
		marketOrder.setValuePaper(s);
		marketOrder.setPortfolio(portfolio1);
		Order limitOrder = new LimitOrder();
		limitOrder.setValuePaper(s);
		limitOrder.setPortfolio(portfolio2);
		
		OrderTransactionEntry transactionEntry1 = new OrderTransactionEntry();
		transactionEntry1.setOrder(marketOrder);
		transactionEntry1.setPortfolio(portfolio1);
		OrderTransactionEntry transactionEntry2 = new OrderTransactionEntry();
		transactionEntry2.setOrder(limitOrder);
		transactionEntry2.setPortfolio(portfolio2);
		
		dataManager.persist(user1);
		dataManager.persist(user2);
		dataManager.persist(portfolio1);
		dataManager.persist(portfolio2);
		dataManager.persist(s);
		dataManager.persist(marketOrder);
		dataManager.persist(limitOrder);
		dataManager.persist(transactionEntry1);
		dataManager.persist(transactionEntry2);
		em.clear();

		// When
		List<OrderTransactionEntry> actual = transactionEntryDataAccess.getOrderTransactionsForValuePaper(code);

		// Then
		Assert.assertUnorderedEquals(Arrays.asList(new OrderTransactionEntry[]{transactionEntry1, transactionEntry2}), actual);
	}
	
	@Test
	public void testGetOrderTransactionsForValuePaper_nonExistent() {
		assertTrue(transactionEntryDataAccess.getOrderTransactionsForValuePaper("ABC").isEmpty());
	}
}
