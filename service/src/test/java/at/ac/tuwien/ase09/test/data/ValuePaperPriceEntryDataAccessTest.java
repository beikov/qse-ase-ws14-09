package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class ValuePaperPriceEntryDataAccessTest extends AbstractContainerTest<ValuePaperPriceEntryDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;
	
	@Test
	public void testGetLastPriceEntry_nonExistentCode(){
		Assert.verifyException(valuePaperPriceEntryDataAccess, EntityNotFoundException.class).getLastPriceEntry("ABC");
	}
	
	@Test
	public void testGetLastPriceEntry(){
		
		// Given
		Stock s = new Stock();
		s.setCode("AT123456");

		Calendar calendar = Calendar.getInstance();
		ValuePaperPriceEntry vpe = new ValuePaperPriceEntry();
		vpe.setCreated(calendar);
		vpe.setPrice(new BigDecimal(5));
		vpe.setValuePaper(s);
		
		dataManager.persist(s);
		dataManager.persist(vpe);
		em.clear();
		
		//When
		ValuePaperPriceEntry actualVpe = valuePaperPriceEntryDataAccess.getLastPriceEntry(s.getCode());
		
		//Then
		assertEquals(vpe, actualVpe);
	}
	
	@Test
	public void testGetHistoricPriceEntryDates_nonExistentCode(){
			
		//When
		List<Calendar> actualHistoricPriceEntryDates = valuePaperPriceEntryDataAccess.getHistoricPriceEntryDates("NonExistentCode");
		
		//Then
		assertTrue(actualHistoricPriceEntryDates.isEmpty());
	}
	
	@Test
	public void testGetHistoricPriceEntryDates(){
		
		// Given
		Stock s = new Stock();
		s.setCode("AT123456");
		
		Calendar calendar1 = Calendar.getInstance();
		ValuePaperHistoryEntry vphe1 = new ValuePaperHistoryEntry();
		vphe1.setDate(calendar1);
		vphe1.setValuePaper(s);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.roll(Calendar.DAY_OF_MONTH, false);
		ValuePaperHistoryEntry vphe2 = new ValuePaperHistoryEntry();
		vphe2.setDate(calendar2);
		vphe2.setValuePaper(s);
		
		dataManager.persist(s);
		dataManager.persist(vphe1);
		dataManager.persist(vphe2);
		em.clear();
		
		//When
		List<Calendar> actualHistoricPriceEntryDates = valuePaperPriceEntryDataAccess.getHistoricPriceEntryDates(s.getCode());
		
		//Then
		assertEquals(actualHistoricPriceEntryDates.size(), 2);
	}

	@Test
	public void testGetValuePaperPriceHistoryEntries_nonExistentCode(){
			
		//When
		List<ValuePaperHistoryEntry> actualHistoricPriceEntries = valuePaperPriceEntryDataAccess.getValuePaperPriceHistoryEntries("NonExistentCode");
		
		//Then
		assertTrue(actualHistoricPriceEntries.isEmpty());
	}
	
	@Test
	public void testGetValuePaperPriceHistoryEntries(){
		
		// Given
		Stock s = new Stock();
		s.setCode("AT123456");
		
		Calendar calendar1 = Calendar.getInstance();
		ValuePaperHistoryEntry vphe1 = new ValuePaperHistoryEntry();
		vphe1.setDate(calendar1);
		vphe1.setValuePaper(s);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.roll(Calendar.DAY_OF_MONTH, false);
		ValuePaperHistoryEntry vphe2 = new ValuePaperHistoryEntry();
		vphe2.setDate(calendar2);
		vphe2.setValuePaper(s);
		
		dataManager.persist(s);
		dataManager.persist(vphe1);
		dataManager.persist(vphe2);
		em.clear();
		
		//When
		List<ValuePaperHistoryEntry> actualHistoricPriceEntries = valuePaperPriceEntryDataAccess.getValuePaperPriceHistoryEntries(s.getCode());
		
		//Then
		assertEquals(actualHistoricPriceEntries.size(), 2);
		assertTrue(actualHistoricPriceEntries.contains(vphe1));
		assertTrue(actualHistoricPriceEntries.contains(vphe2));
	}
	
	@Test
	public void testGetValuePaperHistoryEntriesForPortfolioAfterDate_nullPortfolioCalendar(){
	
		//When
		List<ValuePaperHistoryEntry> actualValuePaperHistoryEntriesForPortfolioAfterDate = valuePaperPriceEntryDataAccess.getValuePaperHistoryEntriesForPortfolioAfterDate(null, null);
		
		//Then
		assertTrue(actualValuePaperHistoryEntriesForPortfolioAfterDate.isEmpty());
	}
	
	@Test
	public void testGetValuePaperHistoryEntriesForPortfolioAfterDate(){
		
		// Given
		User u = new User();
		u.setUsername("username");
		
		Portfolio portfolio = new Portfolio();
		portfolio.setOwner(u);
		
		PortfolioValuePaper pvp = new PortfolioValuePaper();
		pvp.setPortfolio(portfolio);
		
		Stock s = new Stock();
		s.setCode("AT123456");
		pvp.setValuePaper(s);
		portfolio.getValuePapers().add(pvp);
		
		Calendar calendar1 = Calendar.getInstance();
		ValuePaperHistoryEntry vphe1 = new ValuePaperHistoryEntry();
		vphe1.setDate(calendar1);
		vphe1.setValuePaper(s);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.roll(Calendar.YEAR, true);
		ValuePaperHistoryEntry vphe2 = new ValuePaperHistoryEntry();
		vphe2.setDate(calendar2);
		vphe2.setValuePaper(s);
		
		dataManager.persist(u);
		dataManager.persist(s);
		dataManager.persist(portfolio);
		dataManager.persist(pvp);
		dataManager.persist(vphe1);
		dataManager.persist(vphe2);
		em.clear();
		
		//When
		List<ValuePaperHistoryEntry> actualValuePaperHistoryEntriesForPortfolioAfterDate = valuePaperPriceEntryDataAccess.getValuePaperHistoryEntriesForPortfolioAfterDate(portfolio, calendar2);
		
		//Then
		assertEquals(actualValuePaperHistoryEntriesForPortfolioAfterDate.size(), 1);
		assertTrue(actualValuePaperHistoryEntriesForPortfolioAfterDate.contains(vphe2));
	}
	
	@Test
	public void testGetHistoricValuePaperPricesByPortfolioId_nullPortfolioId(){
	
		//When
		List<ValuePaperHistoryEntry> actualHistoricValuePaperPricesByPortfolioId = valuePaperPriceEntryDataAccess.getHistoricValuePaperPricesByPortfolioId(null);
		
		//Then
		assertTrue(actualHistoricValuePaperPricesByPortfolioId.isEmpty());
	}
	
	@Test
	public void testGetHistoricValuePaperPricesByPortfolioId(){
		
		// Given
		User u = new User();
		u.setUsername("username");
		
		Portfolio portfolio = new Portfolio();
		portfolio.setCreated(Calendar.getInstance());
		portfolio.setOwner(u);
		
		PortfolioValuePaper pvp = new PortfolioValuePaper();
		pvp.setPortfolio(portfolio);
		
		Stock s = new Stock();
		s.setCode("AT123456");
		pvp.setValuePaper(s);
		portfolio.getValuePapers().add(pvp);
		
		Calendar calendar1 = Calendar.getInstance();
		calendar1.roll(Calendar.YEAR, true);
		ValuePaperHistoryEntry vphe1 = new ValuePaperHistoryEntry();
		vphe1.setDate(calendar1);
		vphe1.setValuePaper(s);
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.roll(Calendar.YEAR, true);
		ValuePaperHistoryEntry vphe2 = new ValuePaperHistoryEntry();
		vphe2.setDate(calendar2);
		vphe2.setValuePaper(s);
		
		dataManager.persist(u);
		dataManager.persist(s);
		dataManager.persist(portfolio);
		dataManager.persist(pvp);
		dataManager.persist(vphe1);
		dataManager.persist(vphe2);
		em.clear();
		
		//When
		List<ValuePaperHistoryEntry> actualHistoricValuePaperPricesByPortfolioId = valuePaperPriceEntryDataAccess.getHistoricValuePaperPricesByPortfolioId(portfolio.getId());
		
		//Then
		assertEquals(actualHistoricValuePaperPricesByPortfolioId.size(), 2);
		assertTrue(actualHistoricValuePaperPricesByPortfolioId.contains(vphe1));
		assertTrue(actualHistoricValuePaperPricesByPortfolioId.contains(vphe2));
	}
}
