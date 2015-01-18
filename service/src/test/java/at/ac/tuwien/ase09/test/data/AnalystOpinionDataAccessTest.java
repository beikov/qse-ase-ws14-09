package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.data.DividendHistoryEntryDataAccess;
import at.ac.tuwien.ase09.test.AbstractServiceTest;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class AnalystOpinionDataAccessTest extends AbstractServiceTest<AnalystOpinionDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private AnalystOpinionDataAccess analystOpinionDataAccess;

	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(AnalystOpinionDataAccess.class);
	}
	
	@Test
	public void testGetAnalystOpinionsByValuePaperCode_nonExistentAnalystOpinions(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");

		dataManager.persist(stock);

		em.clear();

		// When
		List<AnalystOpinion> actualAnalystOpinionList = analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode(stock.getCode());

		// Then
		assertTrue(actualAnalystOpinionList.isEmpty());
	}

	@Test
	public void testGetAnalystOpinionsByValuePaperCode_nonExistentStock(){
		// When
		List<AnalystOpinion> actualAnalystOpinionList = analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode("NonExistentCode");

		// Then
		assertTrue(actualAnalystOpinionList.isEmpty());
	}

	@Test
	public void testGetAnalystOpinionsByValuePaperCode(){
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
		List<AnalystOpinion> actualAnalystOpinionList = analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode(stock.getCode());

		// Then
		assertEquals(actualAnalystOpinionList.size(), 2);
		assertTrue(actualAnalystOpinionList.contains(analystOpinion1));
		assertTrue(actualAnalystOpinionList.contains(analystOpinion2));
	}
}
