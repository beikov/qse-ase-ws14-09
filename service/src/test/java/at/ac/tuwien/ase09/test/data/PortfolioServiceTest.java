package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.service.PortfolioService;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;


@DatabaseAware
public class PortfolioServiceTest extends AbstractContainerTest<ValuePaperDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private PortfolioService portfolioService;

	@Test
	public void test_savePortfolio_portfolioInDB(){
		//todo
		fail();
	}
	
	@Test 
	public void test_savePortfolio_existingPortfolio_throwsException(){
		//todo
		fail();
	}
	
	@Test
	public void test_existsPortfolioWithNameForUser_forNonExistingPortfolio_returnsFalse(){
		//todo
		fail();
	}
	
	@Test
	public void test_existsPortfolioWithNameForUser_forExistingPortfolio_returnsTrue(){
		//todo
		fail();
	}
	
	@Test
	public void test_existsPortfolioWithNameForUser_forNonExistingUser(){
		//todo
		fail();
	}


}
