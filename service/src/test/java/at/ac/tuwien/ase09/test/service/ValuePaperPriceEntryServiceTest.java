package at.ac.tuwien.ase09.test.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.service.UserService;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class ValuePaperPriceEntryServiceTest extends AbstractServiceTest<ValuePaperPriceEntryServiceTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private ValuePaperPriceEntryService valuePaperPriceService;
	
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceDataAccess;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(
						ValuePaperDataAccess.class,
						ValuePaperPriceEntryService.class,
						ValuePaperPriceEntryDataAccess.class
				);
	}
	
	@Test
	public void testSavePriceEntry(){
		// Given
		final String code = "AT2190589";
		Stock s = new Stock();
		s.setCode(code);
		dataManager.persist(s);
		
		BigDecimal price1 = new BigDecimal(20.5);
		BigDecimal price2 = new BigDecimal(22.6);
		
		// When
		valuePaperPriceService.savePriceEntry(code, price1);
		valuePaperPriceService.savePriceEntry(code, price2);
		
		// Then
		List<ValuePaperPriceEntry> actual = em.createQuery("SELECT v FROM ValuePaperPriceEntry v JOIN FETCH v.valuePaper vp WHERE vp.code = :code ORDER BY v.created ASC").setParameter("code", code).getResultList();
		ValuePaperPriceEntry expectedEntry1 = new ValuePaperPriceEntry();
		expectedEntry1.setValuePaper(s);
		expectedEntry1.setPrice(price1);
		ValuePaperPriceEntry expectedEntry2 = new ValuePaperPriceEntry();
		expectedEntry2.setValuePaper(s);
		expectedEntry2.setPrice(price2);
		assertEquals(expectedEntry1.getValuePaper().getCode(), actual.get(0).getValuePaper().getCode());
		assertEquals(expectedEntry1.getPrice().floatValue(), actual.get(0).getPrice().floatValue(), 0.001);
		assertEquals(expectedEntry2.getValuePaper().getCode(), actual.get(1).getValuePaper().getCode());
		assertEquals(expectedEntry2.getPrice().floatValue(), actual.get(1).getPrice().floatValue(), 0.001);
	}
}
