package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class ValuePaperDataAccessTest extends AbstractServiceTest<ValuePaperDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClass(ValuePaperDataAccess.class);
	}
	
	@Test
	public void testGetValuePaperByCode_nonExistent(){
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getValuePaperByCode("ABC", ValuePaper.class);
	}
	
	@Test
	public void testGetStockByCode(){
		// Given
		Stock s = new Stock();
		s.setCode("AT123456");
		dataManager.persist(s);
		em.clear();
		
		// When
		Stock actual = valuePaperDataAccess.getValuePaperByCode("AT123456", Stock.class);
		
		// Then
		assertEquals(s, actual);
	}
	
	@Test
	public void testGetFundByCode(){
		// Given
		Fund f = new Fund();
		f.setCode("AT123456");
		dataManager.persist(f);
		em.clear();
		
		// When
		Fund actual = valuePaperDataAccess.getValuePaperByCode("AT123456", Fund.class);
		
		// Then
		assertEquals(f, actual);
	}
	
	@Test
	public void testGetStockBondByCode(){
		// Given
		StockBond stockBond = new StockBond();
		stockBond.setCode("AT123456");
		dataManager.persist(stockBond);
		em.clear();
		
		// When
		StockBond actual = valuePaperDataAccess.getValuePaperByCode("AT123456", StockBond.class);
		
		// Then
		assertEquals(stockBond, actual);
	}
	
	@Test
	public void testGetValuePaperByCode(){
		// Given
		StockBond stockBond = new StockBond();
		stockBond.setCode("1");
		Stock stock = new Stock();
		stock.setCode("2");
		Fund fund = new Fund();
		fund.setCode("3");
		dataManager.persist(stockBond);
		dataManager.persist(stock);
		dataManager.persist(fund);
		em.clear();
		
		// When
		StockBond stockBondActual = (StockBond) valuePaperDataAccess.getValuePaperByCode("1", ValuePaper.class);
		Stock stockActual = (Stock) valuePaperDataAccess.getValuePaperByCode("2", ValuePaper.class);
		Fund fundActual = (Fund) valuePaperDataAccess.getValuePaperByCode("3", ValuePaper.class);
		
		// Then
		assertEquals(stockBond, stockBondActual);
		assertEquals(stock, stockActual);
		assertEquals(fund, fundActual);
	}
	
	@Test
	public void testGetStocksByIndex_nonExistent(){
		assertTrue(valuePaperDataAccess.getStocksByIndex("ATX").isEmpty());
	}
	
	@Test
	public void testGetStocksByIndex(){
		// Given
		Stock stockAtx = new Stock();
		stockAtx.setCode("1");
		stockAtx.setIndex("ATX");
		dataManager.persist(stockAtx);
		Stock stockNasdaq = new Stock();
		stockNasdaq.setCode("2");
		stockNasdaq.setIndex("Nasdaq100");
		dataManager.persist(stockNasdaq);
		em.clear();
		
		// When
		List<Stock> actualStocks = valuePaperDataAccess.getStocksByIndex("ATX");
		
		// Then
		
		assertEquals(1, actualStocks.size());
		assertEquals(stockAtx, actualStocks.get(0));
	}

	@Test
	public void testGetStockBondsByBaseStockIndex_nonExistent(){
		assertTrue(valuePaperDataAccess.getStockBondsByBaseStockIndex("ATX").isEmpty());
	}
	
	@Test
	public void testGetStockBondsByBaseStockIndex(){
		// Given
		StockBond stockBondAtx1 = new StockBond();
		stockBondAtx1.setCode("bond1");
		StockBond stockBondAtx2 = new StockBond();
		stockBondAtx2.setCode("bond2");
		StockBond stockBondNasdaq1 = new StockBond();
		stockBondNasdaq1.setCode("bond3");
		
		Stock stockAtx = new Stock();
		stockAtx.setCode("stock1");
		stockAtx.setIndex("ATX");
		Stock stockNasdaq= new Stock();
		stockNasdaq.setCode("stock2");
		stockNasdaq.setIndex("Nasdaq");
		
		stockBondAtx1.setBaseStock(stockAtx);
		stockBondAtx2.setBaseStock(stockAtx);
		stockBondNasdaq1.setBaseStock(stockNasdaq);
		
		dataManager.persist(stockAtx);
		dataManager.persist(stockNasdaq);
		
		dataManager.persist(stockBondAtx1);
		dataManager.persist(stockBondAtx2);
		dataManager.persist(stockBondNasdaq1);
		em.clear();
		
		// When
		List<StockBond> actualStockBonds = valuePaperDataAccess.getStockBondsByBaseStockIndex("ATX");
		
		// Then
		Assert.assertUnorderedEquals(Arrays.asList(new StockBond[]{ stockBondAtx1, stockBondAtx2 }), actualStockBonds);
	}
	
	@Test
	public void testGetValuePapers_nonExistent(){
		assertTrue(valuePaperDataAccess.getValuePapers(ValuePaper.class).isEmpty());
	}
	
	@Test
	public void testGetValuePapers_Stock(){
		// Given
		Stock stock1 = new Stock();
		stock1.setCode("1");
		Stock stock2 = new Stock();
		stock2.setCode("2");
		dataManager.persist(stock1);
		dataManager.persist(stock2);
		em.clear();
		
		// When
		List<Stock> actualStocks = valuePaperDataAccess.getValuePapers(Stock.class);

		// Then
		List<Stock> expectedStocks = Arrays.asList(new Stock[]{ stock1, stock2 });
		Assert.assertUnorderedEquals(expectedStocks, actualStocks);
	}
	
	@Test
	public void testGetValuePapers_ValuePaper(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");
		Fund fund = new Fund();
		fund.setCode("2");
		StockBond bond = new StockBond();
		bond.setCode("3");
		dataManager.persist(stock);
		dataManager.persist(fund);
		dataManager.persist(bond);
		em.clear();
		
		// When
		List<ValuePaper> actualValuePapers = valuePaperDataAccess.getValuePapers(ValuePaper.class);
		
		// Then
		List<ValuePaper> expectedValuePapers = Arrays.asList(new ValuePaper[]{ stock, fund, bond });
		Assert.assertUnorderedEquals(expectedValuePapers, actualValuePapers);
	}
	
	@Test
	public void testGetLatestDividendHistoryEntry_nonExistent(){
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getLatestDividendHistoryEntry("A");
	}
	
	@Test
	public void testGetLatestDividendHistoryEntry(){
		// Given
		Calendar referenceTime = Calendar.getInstance();
		Stock stock = new Stock();
		stock.setCode("1");
		
		DividendHistoryEntry e1 = new DividendHistoryEntry();
		e1.setStock(stock);
		Calendar e1Time = Calendar.getInstance();
		e1Time.setTime(referenceTime.getTime());
		e1.setDividendDate(Calendar.getInstance());
		e1.setDividendDate(e1Time);
		
		DividendHistoryEntry e2 = new DividendHistoryEntry();
		e2.setStock(stock);
		Calendar e2Time = Calendar.getInstance();
		e2Time.setTime(referenceTime.getTime());
		// e2 is older than e1
		e2Time.roll(Calendar.DAY_OF_MONTH, false);
		e2.setDividendDate(Calendar.getInstance());
		e2.setDividendDate(e2Time);
		
		dataManager.persist(stock);
		dataManager.persist(e1);
		dataManager.persist(e2);
		em.clear();
		
		// When
		DividendHistoryEntry actualEntry = valuePaperDataAccess.getLatestDividendHistoryEntry(stock.getCode());
	
		// Then
		assertEquals(e1, actualEntry);
	}
	
	@Test
	public void testGetNewsItemForStockWithTitle_nonExistent(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");
		NewsItem news = new NewsItem();
		news.setTitle("ABC");
		news.setStock(stock);
		
		dataManager.persist(stock);
		dataManager.persist(news);
		em.clear();
		
		// Then
		// for non-existent code
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getNewsItemForStockWithTitle("2", "ABC");
		// for non-existent title
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getNewsItemForStockWithTitle("1", "ABCD");
	}
	
	@Test
	public void testGetNewsItemForStockWithTitle(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");
		NewsItem news = new NewsItem();
		news.setTitle("ABC");
		news.setStock(stock);
		
		dataManager.persist(stock);
		dataManager.persist(news);
		em.clear();
		
		// When
		NewsItem actualNews = valuePaperDataAccess.getNewsItemForStockWithTitle("1", "ABC");
		
		// Then
		assertEquals(news, actualNews);
	}
	
	@Test
	public void testGetAnalystOpinionForStock_nonExistent(){
		// Given
		Calendar created = Calendar.getInstance();
		Stock stock = new Stock();
		stock.setCode("1");
		AnalystOpinion analystOpinion = new AnalystOpinion();
		analystOpinion.setSource("ORF");
		analystOpinion.setCreated(created);
		analystOpinion.setStock(stock);
		
		dataManager.persist(stock);
		dataManager.persist(analystOpinion);
		em.clear();
		
		// Then
		// for non-existent code
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getAnalystOpinionForStock("2", created, "ORF");
		// for non-existent source
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getAnalystOpinionForStock("1", created, "ABCD");
		// non-existent creation date
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getAnalystOpinionForStock("1", Calendar.getInstance(), "ORF");
	}

	@Test
	public void testGetAnalystOpinionForStock(){
		// Given
		Calendar created = Calendar.getInstance();
		Stock stock = new Stock();
		stock.setCode("1");
		AnalystOpinion analystOpinion1 = new AnalystOpinion();
		analystOpinion1.setSource("ORF");
		analystOpinion1.setCreated(created);
		analystOpinion1.setStock(stock);

		AnalystOpinion analystOpinion2 = new AnalystOpinion();
		analystOpinion2.setSource("ARD");
		analystOpinion2.setCreated(created);
		analystOpinion2.setStock(stock);
		
		dataManager.persist(stock);
		dataManager.persist(analystOpinion1);
		dataManager.persist(analystOpinion2);
		em.clear();
		
		// When
		AnalystOpinion actualAnalystOpinion = valuePaperDataAccess.getAnalystOpinionForStock("1", created, "ORF");
		
		// Then
		assertEquals(analystOpinion1, actualAnalystOpinion);
	}
}
