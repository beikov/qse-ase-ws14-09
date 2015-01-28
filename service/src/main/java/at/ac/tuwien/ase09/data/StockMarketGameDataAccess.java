package at.ac.tuwien.ase09.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.Join;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;

@Stateless
public class StockMarketGameDataAccess {

	@Inject
	private EntityManager em;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;


	public List<StockMarketGame> getStockMargetGames() {
		try{
			return em.createQuery("FROM StockMarketGame g JOIN FETCH g.owner", StockMarketGame.class).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<StockMarketGame> getUnstartedStockMarketGames() {
		try{
			return em.createQuery("FROM StockMarketGame g JOIN FETCH g.owner WHERE g.validFrom > NOW()", StockMarketGame.class).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<StockMarketGame> getByInstitutionId(Long institutionId) {
		try{
			return em.createQuery("FROM StockMarketGame g JOIN FETCH g.owner where g.owner.id = :institutionId and g.validTo > :now", StockMarketGame.class).setParameter("institutionId", institutionId).setParameter("now", Calendar.getInstance()).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public  StockMarketGame getStockMarketGameByID(Long id){
		try{

			return em.createQuery("Select s FROM StockMarketGame s "
					+ "LEFT JOIN FETCH s.allowedValuePapers "
					+ "JOIN FETCH s.owner o "
					+ "JOIN FETCH o.admin "
					+ "WHERE s.id = :id", StockMarketGame.class).setParameter("id", id).getSingleResult();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<StockMarketGame> findByNameTextOwner(String name, String text, String owner) {
		try {
			name = name == null ? "" : name.toUpperCase();
			text = text == null ? "" : text.toUpperCase();
			owner = owner == null ? "" : owner.toUpperCase();

			return em.createQuery("FROM StockMarketGame g JOIN FETCH g.owner JOIN FETCH g.owner.admin where UPPER(g.name) like :name and UPPER(g.text) like :text and UPPER(g.owner.name) like :owner", StockMarketGame.class).setParameter("name", "%"+name+"%").setParameter("text", "%"+text+"%").setParameter("owner", "%"+owner+"%").getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}

	public Long getSubscribedUsersCount(StockMarketGame game) {
		try {
			return em.createQuery("SELECT count(*) from Portfolio p where p.game.id = :gameId and p.deleted=false", Long.class).setParameter("gameId", game.getId()).getSingleResult();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}

	/**
	 * 
	 * @param portfolioId The StockMarketGame corresponding to this portfolio or null if there
	 * is not StockMarketGame for this portfolio
	 * @return
	 */
	public StockMarketGame getStockMarketGameForPortfolio(long portfolioId){
		try{
			List<StockMarketGame> stockMarketGames = em.createQuery("SELECT p.game FROM Portfolio p LEFT JOIN p.game g LEFT JOIN FETCH g.allowedValuePapers WHERE p.id = :portfolioId", StockMarketGame.class)
					.setParameter("portfolioId", portfolioId)
					.getResultList();
			if(stockMarketGames.isEmpty()){
				return null;
			}else{
				return stockMarketGames.get(0);
			}
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public long getRankByPortfolio(long portfolioId){

		Map<Currency, BigDecimal> conversionRateMap = new HashMap<>();
		
		List<Portfolio> portfolioList = new ArrayList<Portfolio>();
		Map<Long, Money>portfolioRankingMap = new HashMap<Long, Money>();

		portfolioList = portfolioDataAccess.getPortfoliosByStockMarketGame(portfolioDataAccess.getPortfolioById(portfolioId).getGame().getId());

		for(Portfolio p : portfolioList){
			
			p = portfolioDataAccess.getPortfolioById(p.getId());

			conversionRateMap = portfolioDataAccess.getConversionRateMapForPortfolio(p);
			
			Money sum = createMoney(p.getCurrentCapital().getValue(), p.getCurrentCapital().getCurrency());

			BigDecimal currentValue = portfolioDataAccess.getCurrentValueForPortfolio(p.getId(), conversionRateMap);
			
			Money currentValueForPortfolio;

			if(currentValue != null){
				currentValueForPortfolio = createMoney(currentValue, p.getCurrentCapital().getCurrency());
				sum.setValue(sum.getValue().add(currentValueForPortfolio.getValue()));
			}

			portfolioRankingMap.put(p.getId(), sum);					
			
		}

		Collections.sort(portfolioList, new Comparator<Portfolio>() {
			@Override
			public int compare(Portfolio p1, Portfolio p2)
			{
				return portfolioRankingMap.get(p2.getId()).getValue().subtract(portfolioRankingMap.get(p1.getId()).getValue()).intValue();
			}
		});
		
		for(int i = 0; i < portfolioList.size(); i++){
			if(portfolioList.get(i).getId().equals(portfolioId)){
				return i + 1;
			}
		}
	
		return 0;
	}
	
	private Money createMoney(BigDecimal value, String currencyCode) {
		Money m = new Money();
		m.setCurrency(Currency.getInstance(currencyCode));
		if (value == null)
			value = new BigDecimal(0);
		m.setValue(value);
		return m;
		
	}
	
	private Money createMoney(BigDecimal value, Currency currency) {
		return createMoney(value, currency.getCurrencyCode());
	}

}
