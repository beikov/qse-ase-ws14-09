package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.AnalystRecommendation;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.order.LimitOrder;
import at.ac.tuwien.ase09.model.order.MarketOrder;
import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.order.OrderAction;
import at.ac.tuwien.ase09.model.order.OrderStatus;
import at.ac.tuwien.ase09.model.transaction.OrderFeeTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class PortfolioDataAccessTest extends AbstractContainerTest<PortfolioDataAccessTest> {
	private static final long serialVersionUID = 1L;
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	private BigDecimal startCapital = new BigDecimal("1000.00");
	private Calendar portfolioCreated = Calendar.getInstance();
	
	private Map<String, ValuePaper> valuePapers = new HashMap<>();
	
	private Portfolio portfolio;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Before
	public void createPortfolio() throws ParseException {
		User u = new User();
		u.setUsername("test_user");
		dataManager.persist(u);
		
		PortfolioSetting ps = new PortfolioSetting();
		ps.setStartCapital(new Money(startCapital, Currency.getInstance("EUR")));
		
		Portfolio p = new Portfolio();
		portfolioCreated.setTime(format.parse("2014-12-01"));
		
		p.setSetting(ps);
		p.setName("test_portfolio");
		p.setCreated(portfolioCreated);
		p.setOwner(u);
		
		dataManager.persist(p);
		em.clear();
		
		portfolio = p;
	}
	
	
	
	@Before
	public void initValuePapers() throws ParseException {
		ValuePaper vp = new Stock();
		
		//Stock vp = new Stock();
		vp.setCode("vp1");
		vp.setName("test_valuePaper");
		dataManager.persist(vp);
		
		
		ValuePaperHistoryEntry pe = new ValuePaperHistoryEntry();
		pe.setClosingPrice(new BigDecimal("10.00"));
		Calendar historyDate = Calendar.getInstance();
		historyDate.setTime(format.parse("2014-12-01"));
		pe.setDate(historyDate);
		pe.setValuePaper(vp);
		dataManager.persist(pe);
		
		ValuePaperHistoryEntry pe2 = new ValuePaperHistoryEntry();
		pe2.setClosingPrice(new BigDecimal("11.00"));
		historyDate = Calendar.getInstance();
		historyDate.setTime(format.parse("2014-12-02"));
		pe2.setDate(historyDate);
		pe2.setValuePaper(vp);
		dataManager.persist(pe2);
		
		ValuePaperHistoryEntry pe3 = new ValuePaperHistoryEntry();
		pe3.setClosingPrice(new BigDecimal("12.00"));
		historyDate = Calendar.getInstance();
		historyDate.setTime(format.parse("2014-12-03"));
		pe3.setDate(historyDate);
		pe3.setValuePaper(vp);
		dataManager.persist(pe3);
		
		ValuePaperHistoryEntry pe4 = new ValuePaperHistoryEntry();
		pe4.setClosingPrice(new BigDecimal("12.00"));
		historyDate = Calendar.getInstance();
		historyDate.setTime(format.parse("2014-12-04"));
		pe4.setDate(historyDate);
		pe4.setValuePaper(vp);
		dataManager.persist(pe4);
		
		ValuePaperHistoryEntry pe5 = new ValuePaperHistoryEntry();
		pe5.setClosingPrice(new BigDecimal("15.00"));
		historyDate = Calendar.getInstance();
		historyDate.setTime(format.parse("2014-12-05"));
		pe5.setDate(historyDate);
		pe5.setValuePaper(vp);
		dataManager.persist(pe5);
		
		ValuePaperPriceEntry price = new ValuePaperPriceEntry();
		Calendar priceDate = Calendar.getInstance();
		priceDate.setTime(format.parse("2014-12-01"));
		price.setCreated(priceDate);
		price.setPrice(new BigDecimal("9.50"));
		price.setValuePaper(vp);
		dataManager.persist(price);
		
		ValuePaperPriceEntry price2 = new ValuePaperPriceEntry();
		priceDate = Calendar.getInstance();
		priceDate.setTime(format.parse("2014-12-02"));
		price2.setCreated(priceDate);
		price2.setPrice(new BigDecimal("10.50"));
		price2.setValuePaper(vp);
		dataManager.persist(price2);
		
		ValuePaperPriceEntry price3 = new ValuePaperPriceEntry();
		priceDate = Calendar.getInstance();
		priceDate.setTime(format.parse("2014-12-03"));
		price3.setCreated(priceDate);
		price3.setPrice(new BigDecimal("11.50"));
		price3.setValuePaper(vp);
		dataManager.persist(price3);
		
		ValuePaperPriceEntry price4 = new ValuePaperPriceEntry();
		priceDate = Calendar.getInstance();
		priceDate.setTime(format.parse("2014-12-04"));
		price4.setCreated(priceDate);
		price4.setPrice(new BigDecimal("12.50"));
		price4.setValuePaper(vp);
		dataManager.persist(price4);
		
		ValuePaperPriceEntry price5 = new ValuePaperPriceEntry();
		priceDate = Calendar.getInstance();
		priceDate.setTime(format.parse("2014-12-05"));
		price5.setCreated(priceDate);
		price5.setPrice(new BigDecimal("14.50"));
		price5.setValuePaper(vp);
		dataManager.persist(price5);
		
		ValuePaperPriceEntry price6 = new ValuePaperPriceEntry();
		priceDate = Calendar.getInstance();
		priceDate.setTime(format.parse("2014-12-06"));
		price6.setCreated(priceDate);
		price6.setPrice(new BigDecimal("14.50"));
		price6.setValuePaper(vp);
		dataManager.persist(price6);
		
		valuePapers.put(vp.getCode(), vp);
	}
	
	
	private void addValuePaper(Portfolio p, ValuePaper vp) {
		PortfolioValuePaper pvp = new PortfolioValuePaper();
		pvp.setPortfolio(p);
		pvp.setValuePaper(vp);
		pvp.setVolume(10);
		dataManager.persist(pvp);
		
		Set<PortfolioValuePaper> valuePapers = portfolio.getValuePapers();
		valuePapers.add(pvp);
		p.setValuePapers(valuePapers);
	}
	
	private void buyValuePaper(Portfolio p, ValuePaper vp) throws ParseException {
		
		
		Order order = new LimitOrder();
		Calendar oCreated = Calendar.getInstance();
		oCreated.setTime(format.parse("2014-12-02"));
		order.setCreated(oCreated);
		order.setOrderAction(OrderAction.BUY);
		order.setPortfolio(p);
		order.setStatus(OrderStatus.CLOSED);
		order.setValuePaper(vp);
		order.setVolume(10);
		dataManager.persist(order);
		
		Order order2 = new MarketOrder();
		oCreated = Calendar.getInstance();
		oCreated.setTime(format.parse("2014-12-05"));
		order2.setCreated(oCreated);
		order2.setOrderAction(OrderAction.BUY);
		order2.setPortfolio(p);
		order2.setStatus(OrderStatus.CLOSED);
		order2.setValuePaper(vp);
		order2.setVolume(10);
		dataManager.persist(order2);
		
		Set<Order> orders = p.getOrders();
		orders.add(order);
		orders.add(order2);
		p.setOrders(orders);
		
		OrderFeeTransactionEntry oft = new OrderFeeTransactionEntry();
		Calendar oftCreated = Calendar.getInstance();
		oftCreated.setTime(format.parse("2014-12-02"));
		oft.setCreated(oftCreated);
		oft.setOrder(order);
		oft.setPortfolio(p);
		oft.setValue(new Money(BigDecimal.valueOf(10), Currency.getInstance("EUR")));
		dataManager.persist(oft);
		
		OrderFeeTransactionEntry oft2 = new OrderFeeTransactionEntry();
		oftCreated = Calendar.getInstance();
		oftCreated.setTime(format.parse("2014-12-05"));
		oft2.setCreated(oftCreated);
		oft2.setOrder(order2);
		oft2.setPortfolio(p);
		oft2.setValue(new Money(BigDecimal.valueOf(10), Currency.getInstance("EUR")));
		dataManager.persist(oft2);
		
		OrderTransactionEntry ot = new OrderTransactionEntry();
		Calendar otCreated = Calendar.getInstance();
		otCreated.setTime(format.parse("2014-12-03"));
		ot.setCreated(otCreated);
		ot.setOrder(order);
		ot.setPortfolio(p);
		ot.setValue(new Money(new BigDecimal("115.00"), Currency.getInstance("EUR")));
		dataManager.persist(ot);
		
		PortfolioValuePaper pvp = new PortfolioValuePaper();
		pvp.setPortfolio(p);
		pvp.setValuePaper(vp);
		pvp.setVolume(10);
		Set<PortfolioValuePaper> vpSet = new HashSet<PortfolioValuePaper>();
		vpSet.add(pvp);
		p.setValuePapers(vpSet);
		dataManager.persist(pvp);
		
		OrderTransactionEntry ot2 = new OrderTransactionEntry();
		otCreated = Calendar.getInstance();
		otCreated.setTime(format.parse("2014-12-05"));
		ot2.setCreated(otCreated);
		ot2.setOrder(order);
		ot2.setPortfolio(p);
		ot2.setValue(new Money(new BigDecimal("150.00"), Currency.getInstance("EUR")));
		dataManager.persist(ot2);
		
		pvp.setVolume(20);
		
		Set<TransactionEntry> transactionEntries = p.getTransactionEntries();
		transactionEntries.add(ot);
		transactionEntries.add(ot2);
		transactionEntries.add(oft);
		transactionEntries.add(oft2);
		p.setTransactionEntries(transactionEntries);
		
		em.clear();
	}
	
	@Test
	public void test_getPortfolioValuePaperChange() throws ParseException {
		buyValuePaper(portfolio, valuePapers.get("vp1"));
		double latestPrice = 14.50;
		int volume = 20;
		double payed = 265.;
		double change;
		
		change = (latestPrice*volume - payed) * 100 / payed;
		
		// When
		double actual = portfolioDataAccess.getChange( portfolio.getValuePapers().iterator().next() );
		
		assertEquals(change, actual, 0.0001);
	}
	
	@Test
	public void test_getPortfolioValuePaperProfit() throws ParseException {
		buyValuePaper(portfolio, valuePapers.get("vp1"));
		double latestPrice = 14.50;
		int volume = 20;
		double payed = 265.;
		double profit;
		
		profit = latestPrice*volume - payed;
		
		// When
		double actual = portfolioDataAccess.getProfit( portfolio.getValuePapers().iterator().next() );
		
		assertEquals(profit, actual, 0.0001);
	}
	
	@Test
	public void test_getPortfolioChartEntries_noValuePapers() throws ParseException {
		// Given
		Map<String, BigDecimal> entries = new HashMap<>();
		entries.put("2014-12-01", new BigDecimal("1000.00"));
		
		// When
		Map<String, BigDecimal> actual = portfolioDataAccess.getPortfolioChartEntries(portfolio);
		
		// Then
		assertEquals(entries, actual);
	}
	
	@Test
	public void test_getPortfolioChartEntries() throws ParseException {
		// Given
		buyValuePaper(portfolio, valuePapers.get("vp1"));
		
		Map<String, BigDecimal> entries = new HashMap<>();
		// entry for start capital
		entries.put("2014-12-01", new BigDecimal("1000.00"));
		// entry for order fee
		entries.put("2014-12-02", new BigDecimal("990.00"));
		// entries for history price
		entries.put("2014-12-03", new BigDecimal("995.00"));
		entries.put("2014-12-04", new BigDecimal("995.00"));
		entries.put("2014-12-05", new BigDecimal("1015.00"));
		
		// When
		Map<String, BigDecimal> actual = portfolioDataAccess.getPortfolioChartEntries(portfolio);
		
		// Then
		assertEquals(entries, actual);
		
	}
	
	@Test
	public void test_getNewsForPortfolio_forOneValuePaper() throws ParseException {
		ValuePaper stock = new Stock();
		stock.setCode("abc");
		stock.setName("abc");
		dataManager.persist(stock);
		
		addValuePaper(portfolio, stock);
		
		Calendar date = Calendar.getInstance();
		date.setTime(format.parse("2014-12-01"));
		
		NewsItem news = new NewsItem();
		news.setCreated(date);
		news.setTitle("news for vp1");
		news.setSource("s");
		news.setText("t");
		news.setStock((Stock)stock);
		dataManager.persist(news);
		List<NewsItem> newsList = new ArrayList<>();
		newsList.add(news);
		
		List<NewsItem> actual = portfolioDataAccess.getNewsForPortfolio(portfolio);
		
		assertEquals(newsList.size(), actual.size());
		
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(newsList.get(i), actual.get(i));
		}
		
		
	}
	
	@Test
	public void test_getNewsForPortfolio_forMoreValuePapers() throws ParseException {
		ValuePaper stock = new Stock();
		stock.setCode("abc");
		stock.setName("abc");
		dataManager.persist(stock);
		
		ValuePaper stock2 = new Stock();
		stock2.setCode("def");
		dataManager.persist(stock2);
		
		addValuePaper(portfolio, stock);
		addValuePaper(portfolio, stock2);
		
		
		Calendar date = Calendar.getInstance();
		date.setTime(format.parse("2014-12-01"));
		
		NewsItem news = new NewsItem();
		news.setCreated(date);
		news.setTitle("news for abc");
		news.setStock((Stock)stock);
		dataManager.persist(news);
		
		Calendar date2 = Calendar.getInstance();
		date.setTime(format.parse("2014-12-02"));
		
		NewsItem news2 = new NewsItem();
		news2.setCreated(date2);
		news2.setTitle("news for def");
		news2.setStock((Stock)stock2);
		dataManager.persist(news2);

		List<NewsItem> newsList = new ArrayList<>();
		newsList.add(news);
		newsList.add(news2);
		
		List<NewsItem> actual = portfolioDataAccess.getNewsForPortfolio(portfolio);
		
		assertEquals(newsList.size(), actual.size());
		
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(newsList.get(i), actual.get(i));
		}
		
		
	}
	
	@Test
	public void test_getNewsForPortfolio_withoutExistingNews() throws ParseException {
		buyValuePaper(portfolio, valuePapers.get("vp1"));
		
		List<NewsItem> actual = portfolioDataAccess.getNewsForPortfolio(portfolio);
		
		assertEquals(true, actual.isEmpty());
	}
	
	
	@Test
	public void test_getAnalystOpinionForPortfolio_withoutExistingOpinions() throws ParseException {
		buyValuePaper(portfolio, valuePapers.get("vp1"));
		
		List<AnalystOpinion> actual = portfolioDataAccess.getAnalystOpinionsForPortfolio(portfolio);
		
		assertEquals(true, actual.isEmpty());
	}
	
	@Test
	public void test_getAnalystOpinionForPortfolio_oneValuePaper() throws ParseException {
		ValuePaper stock = new Stock();
		stock.setCode("abc");
		stock.setName("abc");
		dataManager.persist(stock);
		
		addValuePaper(portfolio, stock);
		
		Calendar date = Calendar.getInstance();
		date.setTime(format.parse("2014-12-01"));
		
		AnalystOpinion opinion = new AnalystOpinion();
		opinion.setCreated(date);
		opinion.setRecommendation(AnalystRecommendation.BUY);
		opinion.setStock((Stock)stock);
		dataManager.persist(opinion);
		
		List<AnalystOpinion> opinionList = new ArrayList<>();
		opinionList.add(opinion);
		
		List<AnalystOpinion> actual = portfolioDataAccess.getAnalystOpinionsForPortfolio(portfolio);
		
		assertEquals(1, actual.size());
		assertEquals(opinion, actual.get(0));
	}

	@Test
	public void test_getValuePaperTypeCountMap_portoflioWithoutValuePapers() {
		assertEquals(true, portfolioDataAccess.getValuePaperTypeCountMap(portfolio).isEmpty());
	}
	
	@Test
	public void test_getValuePaperTypeCountMapFrom_portoflioWithOneStock() throws ParseException {
		
		ValuePaper stock = new Stock();
		stock.setCode("abc");
		stock.setName("abc");
		dataManager.persist(stock);
		
		addValuePaper(portfolio, stock);
		
		Map<ValuePaperType, Integer> map = new HashMap<>();
		map.put(ValuePaperType.STOCK, 1);
		
		assertEquals(1, portfolioDataAccess.getValuePaperTypeCountMap(portfolio).size());
	}
	
	@Test
	public void test_getValuePaperCountryCountMap_portoflioWithoutValuePapers() {
		assertEquals(true, portfolioDataAccess.getValuePaperCountryCountMap(portfolio).isEmpty());
	}
	
	
	@Test
	public void test_getValuePaperCountryCountMap_portoflioWithOneStock() throws ParseException {
		
		ValuePaper stock = new Stock();
		stock.setCode("abc");
		stock.setName("abc");
		dataManager.persist(stock);
		
		addValuePaper(portfolio, stock);
		
		Map<ValuePaperType, Integer> map = new HashMap<>();
		map.put(ValuePaperType.STOCK, 1);
		
		assertEquals(1, portfolioDataAccess.getValuePaperCountryCountMap(portfolio).size());
	}
	
	@Test
	public void test_existsPortfolioWithNameForUser_forNonExistingPortfolio_returnsFalse(){
		assertFalse(portfolioDataAccess.existsPortfolioWithNameForUser("not_exists", portfolio.getOwner()));
	}

	@Test
	public void test_existsPortfolioWithNameForUser_forExistingPortfolio_returnsTrue(){
		assertTrue(portfolioDataAccess.existsPortfolioWithNameForUser(portfolio.getName(), portfolio.getOwner()));
	}
	
	@Test
	public void test_getPortfolioByNameForUser__withNonNullInputs(){
		assertEquals(null, portfolioDataAccess.getPortfolioByNameForUser(null, null));
	}	
	
}
