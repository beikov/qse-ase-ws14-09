package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.data.NewsItemDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.TransactionEntryDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class NewsItemDataAccessTest extends AbstractServiceTest<NewsItemDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private NewsItemDataAccess newItemDataAccess;

	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(NewsItemDataAccess.class);
	}

	@Test
	public void testGetNewsItemsByValuePaperCode_nonExistentNewsItems(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");

		dataManager.persist(stock);
		em.clear();

		// When
		List<NewsItem> actualNewsItemList = newItemDataAccess.getNewsItemsByValuePaperCode(stock.getCode());

		// Then
		assertTrue(actualNewsItemList.isEmpty());
	}
	
	@Test
	public void testGetNewsItemsByValuePaperCode_nonExistentStock(){
		// When
		List<NewsItem> actualNewsItemList = newItemDataAccess.getNewsItemsByValuePaperCode("NonExistentCode");

		// Then
		assertTrue(actualNewsItemList.isEmpty());
	}

	@Test
	public void testGetNewsItemsByValuePaperCode(){
		// Given
		Stock stock = new Stock();
		stock.setCode("1");
		NewsItem news1 = new NewsItem();
		news1.setTitle("ABC");
		news1.setStock(stock);
		NewsItem news2 = new NewsItem();
		news2.setTitle("DEF");
		news2.setStock(stock);

		dataManager.persist(stock);
		dataManager.persist(news1);
		dataManager.persist(news2);
		em.clear();

		// When
		List<NewsItem> actualNewsItemList = newItemDataAccess.getNewsItemsByValuePaperCode(stock.getCode());

		// Then
		assertEquals(actualNewsItemList.size(), 2);
		assertTrue(actualNewsItemList.contains(news1));
		assertTrue(actualNewsItemList.contains(news2));
	}
}
