package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class ValuePaperDataAccessTest extends AbstractContainerTest<ValuePaperDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	
	@Test(expected=EntityNotFoundException.class)
	public void testGetValuePaperByNonExistentIsin(){
		valuePaperDataAccess.getValuePaperByCode("ABC", ValuePaper.class);
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
		em.clear();
		dataManager.persist(f);
		em.clear();
		
		// When
		Fund actual = valuePaperDataAccess.getValuePaperByCode("AT123456", Fund.class);
		
		// Then
		assertEquals(f, actual);
	}
}
