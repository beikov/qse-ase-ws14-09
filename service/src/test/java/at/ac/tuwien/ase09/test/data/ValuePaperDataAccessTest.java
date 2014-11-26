package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.AssertTrue;

import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class ValuePaperDataAccessTest extends AbstractContainerTest<ValuePaperDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	
	@Test
	public void testGetValuePaperByCode_NonExistent(){
		Assert.verifyException(valuePaperDataAccess, EntityNotFoundException.class).getValuePaperByCode("ABC", ValuePaper.class);
	}
	
	@Test
	public void testGetStockByCode(){
		//Given
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
		//Given
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
		//Given
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
		//Given
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
	public void testGetStocksByIndex_NoStocks(){
		assertTrue(valuePaperDataAccess.getStocksByIndex("ATX").isEmpty());
	}
	
	@Test
	public void testGetStocksByIndex(){
		//Given
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
	public void testGetStockBondsByBaseStockIndex(){
		//Given
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
	
}
