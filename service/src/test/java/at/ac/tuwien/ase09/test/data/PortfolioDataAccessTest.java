package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;


@DatabaseAware
public class PortfolioDataAccessTest extends AbstractContainerTest<ValuePaperDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Test
	public void test_getPortfolioByNameForUser_withCorrectInputs_returnsPortfolio(){
		//todo
		fail();
	}
	
	public void test_getPortfolioByNameForUser__withNonExistingUser(){
		//todo
		fail();
	}
	
	public void test_getPortfolioByNameForUser__withNonExistingPortfolioName(){
		//todo
		fail();
	}
	
	public void test_getPortfolioByNameForUser__withNonNullInputs(){
		//todo
		fail();
	}	
		

}
