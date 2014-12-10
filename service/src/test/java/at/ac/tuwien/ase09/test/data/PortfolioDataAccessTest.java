package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

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


@DatabaseAware
public class PortfolioDataAccessTest extends AbstractContainerTest<ValuePaperDataAccessTest>{
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
		portfolio.setName("pf1");
		
		owner = new User();
		owner.setUsername("owner");
		userService.saveUser(owner);
		
		portfolio.setOwner(owner);
		portfolioService.savePortfolio(portfolio);
	}

	
	@Test
	public void test_getPortfolioByNameForUser_withCorrectInputs(){
		assertEquals(portfolio, portfolioDataAccess.getPortfolioByNameForUser("pf1", owner));
	}
	
	@Test
	public void test_getPortfolioByNameForUser__withWrongName(){
		assertEquals(null,portfolioDataAccess.getPortfolioByNameForUser("wrong_name", owner));
	}
	
	
	@Test
	public void test_getPortfolioByNameForUser__withNonNullInputs(){
		assertEquals(null, portfolioDataAccess.getPortfolioByNameForUser(null, null));
	}	
		

}