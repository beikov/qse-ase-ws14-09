package at.ac.tuwien.ase09.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.order.MarketOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderStatus;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class PortfolioServiceTest extends AbstractContainerTest<PortfolioServiceTest> {
	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	@Inject
	private PortfolioService portfolioService;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	private void createPortfolio() {
		
	}
	
	@Test
	public void testGetPortfolioChartEntriesNoValuePapers() throws ParseException {
		// Given
		User u = new User();
		u.setUsername("test_user");
		dataManager.persist(u);
		
		BigDecimal startCapital = BigDecimal.valueOf(1000);
		PortfolioSetting ps = new PortfolioSetting();
		ps.setStartCapital(new Money(startCapital, Currency.getInstance(Locale.GERMANY)));
		
		Portfolio p = new Portfolio();
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse("2014-04-13"));
		
		p.setSetting(ps);
		p.setName("test_portfolio");
		p.setCreated(c);
		p.setOwner(u);
		
		dataManager.persist(p);
		em.clear();
		
		Map<String, BigDecimal> should = new HashMap<>();
		should.put(format.format(c.getTime()), startCapital);
		
		// When
		Map<String, BigDecimal> actual = portfolioService.getPortfolioChartEntries(p);
		
		// Then
		assertEquals(should, actual);
	}
	
	@Test
	public void testGetPortfolioChartEntries() {
		//em.getTransaction().begin();
		// Given
		Calendar c = Calendar.getInstance();
		
		User u = new User();
		u.setUsername("test_user");
		dataManager.persist(u);
		
		BigDecimal startCapital = new BigDecimal("1000.00");
		PortfolioSetting ps = new PortfolioSetting();
		ps.setStartCapital(new Money(startCapital, Currency.getInstance(Locale.GERMANY)));
		
		Portfolio p = new Portfolio();
		p.setSetting(ps);
		p.setName("test_portfolio");
		p.setCreated(c);
		p.setOwner(u);
		dataManager.persist(p);
		
		Stock vp = new Stock();
		vp.setCode("A1234");
		vp.setName("test_valuePaper");
		dataManager.persist(vp);
		
		ValuePaperHistoryEntry pe = new ValuePaperHistoryEntry();
		BigDecimal closingPrice = BigDecimal.valueOf(13.37);
		pe.setClosingPrice(closingPrice);
		pe.setDate(c);
		pe.setValuePaper(vp);
		dataManager.persist(pe);
		
		PortfolioValuePaper pvp = new PortfolioValuePaper();
		pvp.setPortfolio(p);
		pvp.setValuePaper(vp);
		HashSet<PortfolioValuePaper> vpSet = new HashSet<PortfolioValuePaper>();
		vpSet.add(pvp);
		p.setValuePapers(vpSet);
		dataManager.persist(pvp);
		
		MarketOrder order = new MarketOrder();
		order.setCreated(c);
		order.setOrderAction(OrderAction.BUY);
		order.setPortfolio(p);
		order.setStatus(OrderStatus.CLOSED);
		order.setValuePaper(vp);
		order.setVolume(10);
		dataManager.persist(order);
		
		HashSet<Order> orders = new HashSet<>();
		orders.add(order);
		p.setOrders(orders);
		
		OrderTransactionEntry ot = new OrderTransactionEntry();
		ot.setCreated(c);
		ot.setOrder(order);
		ot.setPortfolio(p);
		ot.setValue(new Money(BigDecimal.valueOf(300), Currency.getInstance("EUR")));
		dataManager.persist(ot);
		
		HashSet<TransactionEntry> transactionEntries = new HashSet<>();
		transactionEntries.add(ot);
		p.setTransactionEntries(transactionEntries);
		
		em.clear();
		
		Map<String, BigDecimal> should = new HashMap<>();
		should.put(format.format(c.getTime()), startCapital);
		
		// When
		Portfolio actualPortfolio = portfolioDataAccess.getPortfolioById(p.getId()); 
		Map<String, BigDecimal> actual = portfolioService.getPortfolioChartEntries(actualPortfolio);
		
		
		
		// Then
		assertEquals(should, actual);
		
	}

}
