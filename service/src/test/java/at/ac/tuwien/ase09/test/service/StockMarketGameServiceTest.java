package at.ac.tuwien.ase09.test.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.StockMarketGameService;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class StockMarketGameServiceTest extends AbstractServiceTest<StockMarketGameServiceTest>{

	private static final long serialVersionUID = 1L;

	@Inject
	StockMarketGameService stockMarketGameService;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(StockMarketGameService.class);
	}
	
	@Test
	public void testSaveStockMarketGame(){
		// Given
		
		StockMarketGame stockMarketGame = new StockMarketGame();
		
		stockMarketGame.setName("StockMarketGame1");
		stockMarketGame.setRegistrationFrom(Calendar.getInstance());
		stockMarketGame.setRegistrationTo(Calendar.getInstance());
		stockMarketGame.setValidFrom(Calendar.getInstance());
		stockMarketGame.setValidTo(Calendar.getInstance());
		stockMarketGame.setText("Text");
		
		PortfolioSetting portfolioSetting = new PortfolioSetting();
		
		portfolioSetting.setCapitalReturnTax(new BigDecimal(0.5));
		portfolioSetting.setOrderFee(new Money(new BigDecimal(2), Currency.getInstance("EUR")));
		portfolioSetting.setPortfolioFee(new Money(new BigDecimal(10), Currency.getInstance("EUR")));
		portfolioSetting.setStartCapital(new Money(new BigDecimal(1000), Currency.getInstance("EUR")));
		
		stockMarketGame.setSetting(portfolioSetting);
		
		User admin = new User();
		admin.setUsername("admin");
		
		Institution institution = new Institution();
		
		institution.setAdmin(admin);
		institution.setName("Institution1");
		
		stockMarketGame.setOwner(institution);

		dataManager.persist(admin);
		dataManager.persist(institution);
		em.clear();
		
		// When
		stockMarketGameService.saveStockMarketGame(stockMarketGame);
		
		// Then
		StockMarketGame actual = em.createQuery("SELECT s FROM StockMarketGame s JOIN FETCH s.owner WHERE s.name = :name", StockMarketGame.class).setParameter("name", stockMarketGame.getName()).getSingleResult();
		assertEquals(stockMarketGame.getId(), actual.getId());
		assertEquals(stockMarketGame.getName(), actual.getName());
		assertEquals(stockMarketGame.getOwner(), actual.getOwner());
		assertEquals(stockMarketGame.getSetting().getCapitalReturnTax().longValue(), actual.getSetting().getCapitalReturnTax().longValue());
	}
}
