package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.assertEquals;

import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class StockMarketGameDataAccessTest extends AbstractServiceTest<StockMarketGameDataAccessTest>{
private static final long serialVersionUID = 1L;
	
	@Inject
	private StockMarketGameDataAccess stockMarketGameDataAccess;
	
	private StockMarketGame game2=new StockMarketGame();
	
	@Before
	public void  init()
	{
		User user=new User();
		user.setUsername("user100");
		
		dataManager.persist(user);
		
		Institution inst=new Institution();
		inst.setName("Erste Bank");
		inst.setPageText("Eine Institution");
		inst.setAdmin(user);
		
		dataManager.persist(inst);
		
		Stock stockAtx = new Stock();
		stockAtx.setCode("stock1");
		stockAtx.setIndex("ATX");
		stockAtx.setCountry("�sterreich");
		stockAtx.setCurrency(Currency.getInstance("EUR"));
		stockAtx.setName("Andritz");
		
		Stock stockNasdaq = new Stock();
		stockNasdaq.setCode("stock2");
		stockNasdaq.setIndex("Nasdaq100");	
		stockNasdaq.setCountry("USA");
		stockNasdaq.setCurrency(Currency.getInstance("USD"));
		stockNasdaq.setName("Google");
		
		Fund f = new Fund();
		f.setCode("fund1");
		f.setName("Superfund");
		f.setCurrency(Currency.getInstance("EUR"));
		dataManager.persist(f);
		dataManager.persist(stockAtx);
		dataManager.persist(stockNasdaq);
		
		Set<ValuePaper> allowedValuePapers=new HashSet<ValuePaper>();
		allowedValuePapers.add(stockAtx);
		allowedValuePapers.add(stockNasdaq);
		
		
		StockMarketGame game=new StockMarketGame();
		game.setName("Erste Bank Spiel");
		game.setOwner(inst);
		game.setText("Ein B�rsenspiel");
		game.setAllowedValuePapers(allowedValuePapers);
		dataManager.persist(game);
		
		allowedValuePapers.add(f);
		game2.setName("Erste Bank Spiel2");
		game2.setOwner(inst);
		game2.setText("Ein anderes B�rsenspiel");
		game2.setAllowedValuePapers(allowedValuePapers);
		dataManager.persist(game2);
		
	}
	@Test
	public void testStockMarketGameByID()
	{
		StockMarketGame smg=stockMarketGameDataAccess.getStockMarketGameByID(game2.getId());
		
		assertEquals(game2.getId(),smg.getId());
		assertEquals("Erste Bank Spiel2",smg.getName());
		assertEquals("Ein anderes B�rsenspiel",smg.getText());
		assertEquals(3,smg.getAllowedValuePapers().size());
	}
	
	@Test(expected=EntityNotFoundException.class)
	public void testStockMarketGameByID_Exception()
	{
		StockMarketGame smg=stockMarketGameDataAccess.getStockMarketGameByID((long) 100);
		
		assertEquals(null,smg);
	}
}
