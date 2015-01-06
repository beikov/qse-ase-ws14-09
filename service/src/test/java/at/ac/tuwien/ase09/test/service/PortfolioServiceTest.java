package at.ac.tuwien.ase09.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.data.NewsItemDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.TransactionEntryDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioVisibility;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.PortfolioService;
import at.ac.tuwien.ase09.service.UserService;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;


@DatabaseAware
public class PortfolioServiceTest extends AbstractServiceTest<PortfolioServiceTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private PortfolioService portfolioService;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserService userService;

	private Portfolio portfolio;
	private User owner;

	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(
						ValuePaperPriceEntryDataAccess.class,
						NewsItemDataAccess.class,
						AnalystOpinionDataAccess.class,
						TransactionEntryDataAccess.class,
						PortfolioService.class,
						PortfolioDataAccess.class,
						UserService.class);
	}
	
	@Before
	public void init(){
		portfolio = new Portfolio();
		portfolio.setVisibility(new PortfolioVisibility());
		owner = new User();
		owner.setUsername("owner");
		userService.saveUser(owner);
	}

	@Test
	public void test_savePortfolio_withoutOwner_throwsException(){
		portfolio.setName("pf1");
		Assert.verifyException(portfolioService, PersistenceException.class).savePortfolio(portfolio);
	}

	@Test
	public void test_savePortfolio_portfolioInDB(){
		portfolio.setName("pf1");
		portfolio.setOwner(owner);
		portfolioService.savePortfolio(portfolio);

		assertTrue(portfolioDataAccess.getPortfolios().contains(portfolio));
	}
	
	@Test
	public void testSaveExistingPortfolio(){
		// Given
		User user = new User();
		Portfolio portfolio = new Portfolio();
		portfolio.setOwner(user);
		portfolio.setName("MyPortfolio");
		
		dataManager.persist(user);
		dataManager.persist(portfolio);
		em.clear();
		
		// When
		final String newName = "MyPortfolio2";
		portfolio.setName(newName);
		portfolioService.savePortfolio(portfolio);
		
		// Then
		Portfolio actual = portfolioDataAccess.getPortfolioByNameForUser(newName, user);
		assertEquals(newName, actual.getName());
	}
	
}
