package at.ac.tuwien.ase09.test.service;

import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioVisibility;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.PortfolioService;
import at.ac.tuwien.ase09.service.UserService;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;
import at.ac.tuwien.ase09.test.data.ValuePaperDataAccessTest;


@DatabaseAware
public class PortfolioServiceTest extends AbstractContainerTest<ValuePaperDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private PortfolioService portfolioService;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserService userService;

	private Portfolio portfolio;
	private User owner;

	@Before
	public void init(){
		portfolio = new Portfolio();
		portfolio.setVisibility(new PortfolioVisibility());
		owner = new User();
		owner.setUsername("owner");
		userService.saveUser(owner);
	}

	@Test(expected=PersistenceException.class)
	public void test_savePortfolio_withoutOwner_throwsException(){
		portfolio.setName("pf1");
		portfolioService.savePortfolio(portfolio);
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
