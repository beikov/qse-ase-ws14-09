package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
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
	
	@Test
	public void testGetDayHighPrice_nonExistent(){
		assertEquals(null, valuePaperPriceEntryDataAccess.getDayHighPrice("ABC"));
	}
	
	@Test
	public void testGetDayHighPrice(){
		// Given
		Stock s = new Stock();
		s.setCode("ABC");
		Fund f = new Fund();
		f.setCode("EFG");
		
		final float expectedPrice = 24.6f;
		final Calendar today1 = Calendar.getInstance();
		today1.set(Calendar.HOUR, 5);
		final Calendar today2 = Calendar.getInstance();
		today2.setTime(today1.getTime());
		today2.set(Calendar.HOUR, 15);
		today2.set(Calendar.MILLISECOND, today2.getMinimum(Calendar.MILLISECOND));
		today2.set(Calendar.SECOND, today2.getMinimum(Calendar.SECOND));
		today2.set(Calendar.MINUTE, today2.getMinimum(Calendar.MINUTE));
		today2.set(Calendar.HOUR, today2.getMinimum(Calendar.HOUR));
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(today1.getTime());
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		yesterday.set(Calendar.MILLISECOND, yesterday.getMaximum(Calendar.MILLISECOND));
		yesterday.set(Calendar.SECOND, yesterday.getMaximum(Calendar.SECOND));
		yesterday.set(Calendar.MINUTE, yesterday.getMaximum(Calendar.MINUTE));
		yesterday.set(Calendar.HOUR, yesterday.getMaximum(Calendar.HOUR));
		
		ValuePaperPriceEntry pe1 = new ValuePaperPriceEntry();
		pe1.setCreated(today1);
		pe1.setValuePaper(s);
		pe1.setPrice(new BigDecimal("24.5"));
		ValuePaperPriceEntry pe2 = new ValuePaperPriceEntry();
		pe2.setCreated(today2);
		pe2.setValuePaper(s);
		pe2.setPrice(new BigDecimal(expectedPrice));
		ValuePaperPriceEntry pe3 = new ValuePaperPriceEntry();
		pe3.setCreated(today2);
		pe3.setValuePaper(f);
		pe3.setPrice(new BigDecimal("22.5"));
		ValuePaperPriceEntry pe4 = new ValuePaperPriceEntry();
		pe4.setCreated(yesterday);
		pe4.setValuePaper(s);
		pe4.setPrice(new BigDecimal("26.5"));
		ValuePaperPriceEntry pe5 = new ValuePaperPriceEntry();
		pe5.setCreated(yesterday);
		pe5.setValuePaper(f);
		pe5.setPrice(new BigDecimal("21.5"));
		
		dataManager.persist(s);
		dataManager.persist(f);
		dataManager.persist(pe1);
		dataManager.persist(pe2);
		dataManager.persist(pe3);
		dataManager.persist(pe4);
		dataManager.persist(pe5);
		em.clear();
			
		// When
		BigDecimal actualDayHighPrice = valuePaperPriceEntryDataAccess.getDayHighPrice(s.getCode());
	
		// Then
		assertEquals(expectedPrice, actualDayHighPrice.floatValue(), 0.001);
	}
	
	@Test
	public void testGetDayLowPrice_nonExistent(){
		assertEquals(null, valuePaperPriceEntryDataAccess.getDayLowPrice("ABC"));
	}
	
	@Test
	public void testGetDayLowPrice(){
		// Given
		Stock s = new Stock();
		s.setCode("ABC");
		Fund f = new Fund();
		f.setCode("EFG");
		
		final float expectedPrice = 24.5f;
		final Calendar today1 = Calendar.getInstance();
		today1.set(Calendar.HOUR, 5);
		final Calendar today2 = Calendar.getInstance();
		today2.setTime(today1.getTime());
		today2.set(Calendar.HOUR, 15);
		today2.set(Calendar.MILLISECOND, today2.getMinimum(Calendar.MILLISECOND));
		today2.set(Calendar.SECOND, today2.getMinimum(Calendar.SECOND));
		today2.set(Calendar.MINUTE, today2.getMinimum(Calendar.MINUTE));
		today2.set(Calendar.HOUR, today2.getMinimum(Calendar.HOUR));
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(today1.getTime());
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		yesterday.set(Calendar.MILLISECOND, yesterday.getMaximum(Calendar.MILLISECOND));
		yesterday.set(Calendar.SECOND, yesterday.getMaximum(Calendar.SECOND));
		yesterday.set(Calendar.MINUTE, yesterday.getMaximum(Calendar.MINUTE));
		yesterday.set(Calendar.HOUR, yesterday.getMaximum(Calendar.HOUR));
		
		ValuePaperPriceEntry pe1 = new ValuePaperPriceEntry();
		pe1.setCreated(today1);
		pe1.setValuePaper(s);
		pe1.setPrice(new BigDecimal(expectedPrice));
		ValuePaperPriceEntry pe2 = new ValuePaperPriceEntry();
		pe2.setCreated(today2);
		pe2.setValuePaper(s);
		pe2.setPrice(new BigDecimal("24.6"));
		ValuePaperPriceEntry pe3 = new ValuePaperPriceEntry();
		pe3.setCreated(today2);
		pe3.setValuePaper(f);
		pe3.setPrice(new BigDecimal("22.5"));
		ValuePaperPriceEntry pe4 = new ValuePaperPriceEntry();
		pe4.setCreated(yesterday);
		pe4.setValuePaper(s);
		pe4.setPrice(new BigDecimal("26.5"));
		ValuePaperPriceEntry pe5 = new ValuePaperPriceEntry();
		pe5.setCreated(yesterday);
		pe5.setValuePaper(f);
		pe5.setPrice(new BigDecimal("21.5"));
		
		dataManager.persist(s);
		dataManager.persist(f);
		dataManager.persist(pe1);
		dataManager.persist(pe2);
		dataManager.persist(pe3);
		dataManager.persist(pe4);
		dataManager.persist(pe5);
		em.clear();
			
		// When
		BigDecimal actualDayLowPrice = valuePaperPriceEntryDataAccess.getDayLowPrice(s.getCode());
	
		// Then
		assertEquals(expectedPrice, actualDayLowPrice.floatValue(), 0.001);
	}
	
	@Test
	public void testGetHistoricPriceEntry(){
		// Given
		Stock s = new Stock();
		s.setCode("ABC");
		
		final float expectedClosingPrice = 32.5f;
		ValuePaperHistoryEntry historyEntry = new ValuePaperHistoryEntry();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		historyEntry.setValuePaper(s);
		historyEntry.setClosingPrice(new BigDecimal(expectedClosingPrice));
		historyEntry.setDate(yesterday);
		
		dataManager.persist(s);
		dataManager.persist(historyEntry);
		em.clear();
		
		// When
		ValuePaperHistoryEntry actualHistoryEntry = valuePaperPriceEntryDataAccess.getHistoricPriceEntry(s.getCode(), yesterday);
	
		// Then
		assertEquals(expectedClosingPrice, actualHistoryEntry.getClosingPrice().floatValue(), 0.001f);
	}
	
	@Test
	public void testGetHistoricPriceEntry_nonExistent(){
		// Given
		Stock s = new Stock();
		s.setCode("ABC");
		
		final float expectedClosingPrice = 32.5f;
		ValuePaperHistoryEntry historyEntry = new ValuePaperHistoryEntry();
		Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		historyEntry.setValuePaper(s);
		historyEntry.setClosingPrice(new BigDecimal(expectedClosingPrice));
		historyEntry.setDate(yesterday);
		
		dataManager.persist(s);
		dataManager.persist(historyEntry);
		em.clear();
		
		// Then
		Assert.verifyException(valuePaperPriceEntryDataAccess, EntityNotFoundException.class).getHistoricPriceEntry(s.getCode(), Calendar.getInstance());
	}
}
