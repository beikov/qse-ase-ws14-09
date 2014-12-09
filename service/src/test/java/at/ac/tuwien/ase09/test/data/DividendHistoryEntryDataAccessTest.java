package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.data.DividendHistoryEntryDataAccess;
import at.ac.tuwien.ase09.data.NewsItemDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.Assert;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.test.DatabaseAware;
import at.ac.tuwien.ase09.test.AbstractContainerTest;

public class DividendHistoryEntryDataAccessTest extends AbstractContainerTest<DividendHistoryEntryDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private DividendHistoryEntryDataAccess dividendHistoryEntryDataAccess;


	@Test
	public void testGetDividendHistoryEntryByValuePaperCode_nonExistentDividends(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");

		dataManager.persist(stock);
		em.clear();
		
		// When
		List<DividendHistoryEntry> actualDividendHistoryEntryList = dividendHistoryEntryDataAccess.getDividendHistoryEntryByValuePaperCode(stock.getCode());

		// Then
		assertTrue(actualDividendHistoryEntryList.isEmpty());
	}

	@Test
	public void testGetDividendHistoryEntryByValuePaperCode_nonExistentStock(){
		// When
		List<DividendHistoryEntry> actualDividendHistoryEntryList = dividendHistoryEntryDataAccess.getDividendHistoryEntryByValuePaperCode("NonExistentCode");

		// Then
		assertTrue(actualDividendHistoryEntryList.isEmpty());
	}
	
	@Test
	public void testGetDividendHistoryEntryByValuePaperCode(){
		// Given
		Calendar referenceTime = Calendar.getInstance();
		Stock stock = new Stock();
		stock.setCode("3");
		
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
		List<DividendHistoryEntry> actualDividendHistoryEntryList = dividendHistoryEntryDataAccess.getDividendHistoryEntryByValuePaperCode(stock.getCode());

		// Then
		assertEquals(actualDividendHistoryEntryList.size(), 2);
		assertTrue(actualDividendHistoryEntryList.contains(e1));
		assertTrue(actualDividendHistoryEntryList.contains(e2));
	}
}
