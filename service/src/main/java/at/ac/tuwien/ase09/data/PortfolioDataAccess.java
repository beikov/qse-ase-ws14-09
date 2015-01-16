package at.ac.tuwien.ase09.data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionType;

@Stateless
public class PortfolioDataAccess {

	@Inject
	private EntityManager em;
	
	@Inject
	private ValuePaperPriceEntryDataAccess priceDataAccess;
	
	@Inject
	private NewsItemDataAccess newsItemDataAccess;
	
	@Inject
	private AnalystOpinionDataAccess analystOpinionDataAccess;
	
	@Inject
	private TransactionEntryDataAccess transactionDataAccess;
	
	public List<Portfolio> getPortfolios() {
		try{
			return em.createQuery("FROM Portfolio", Portfolio.class).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public List<Portfolio> getPortfoliosByUser(User user) {
		try{
			return em.createQuery("FROM Portfolio p WHERE p.owner = :user", Portfolio.class).setParameter("user", user).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public Portfolio getPortfolioById(Long id) {
		try {
			return em.createQuery("FROM Portfolio p "
					+ "LEFT JOIN FETCH p.valuePapers "
					+ "LEFT JOIN FETCH p.transactionEntries "
					+ "LEFT JOIN FETCH p.orders o "
					+ "LEFT JOIN FETCH o.valuePaper "
					+ "JOIN FETCH p.owner "
					+ "LEFT JOIN FETCH p.followers "
					+ "WHERE p.id = :id", Portfolio.class).setParameter("id", id).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public Portfolio getPortfolioByUsernameAndId(String username, Long id) {
		try {
			return em.createQuery("FROM Portfolio p "
					+ "LEFT JOIN FETCH p.valuePapers "
					+ "LEFT JOIN FETCH p.transactionEntries "
					+ "LEFT JOIN FETCH p.orders o "
					+ "LEFT JOIN FETCH o.valuePaper "
					+ "JOIN FETCH p.owner "
					+ "LEFT JOIN FETCH p.followers "
					+ "WHERE p.id = :id AND p.owner.username = :username", Portfolio.class).setParameter("username", username).setParameter("id", id).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public Portfolio getPortfolioByNameForUser(String portfolioName, User user){
		try{
			List<Portfolio> results;
			results = em.createQuery("FROM Portfolio p WHERE p.owner = :user AND p.name = :name", Portfolio.class).setParameter("user", user).setParameter("name", portfolioName).getResultList();
			if (results.isEmpty()){
				return null;
			}else{
				return results.get(0);
			}
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public Portfolio getByGameAndUser(StockMarketGame game, User user) {
		try {
			return em.createQuery("FROM Portfolio p "
					+ "LEFT JOIN FETCH p.game "
					+ "JOIN FETCH p.owner "
					+ "WHERE p.game.id = :gameId AND p.owner.id = :ownerId", Portfolio.class).setParameter("gameId", game.getId()).setParameter("ownerId", user.getId()).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	
	
	public Map<ValuePaperType, Integer> getValuePaperTypeCountMap(Portfolio portfolio) {
		Map<ValuePaperType, Integer> valuePaperTypeCounterMap = new HashMap<ValuePaperType, Integer>();
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			ValuePaper paper = pvp.getValuePaper();
			ValuePaperType type = paper.getType();
			int old = 0;
			if (valuePaperTypeCounterMap.get(type) != null)
				old = valuePaperTypeCounterMap.get(type);
			valuePaperTypeCounterMap.put(type, ++old);
		}
		return valuePaperTypeCounterMap;
	}
	
	public Map<String, Integer> getValuePaperCountryCountMap(Portfolio portfolio) {
		Map<String, Integer> valuePaperCountryCountMap = new HashMap<String, Integer>(); 
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			ValuePaper paper = pvp.getValuePaper();
			if (!(paper instanceof Stock)) {
				continue;
			}
			Stock stock = (Stock)paper;
			String country = stock.getCountry();
			int old = 0;
			if (valuePaperCountryCountMap.get(country) != null)
				old = valuePaperCountryCountMap.get(country);
			valuePaperCountryCountMap.put(country, ++old);
		}
		return valuePaperCountryCountMap;
	}
	
	public boolean existsPortfolioWithNameForUser(String portfolioName, User user) {
		return getPortfolioByNameForUser(portfolioName, user) != null;
	}

	public BigDecimal getTotalPayedForValuePaper(String code) {
		
		List<OrderTransactionEntry> orderTransactions = transactionDataAccess.getOrderTransactionsForValuePaper(code);
		
		BigDecimal payed = new BigDecimal(0);
		for (OrderTransactionEntry ot : orderTransactions) {
			payed = payed.add(ot.getValue().getValue());
		}
		return payed;
	}

	public Map<String, BigDecimal> getPortfolioChartEntries(Portfolio portfolio) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		BigDecimal startCapital = portfolio.getSetting().getStartCapital().getValue();
		Map<String, BigDecimal> changeMap = new HashMap<>();
		Map<String, BigDecimal> changeCarryMap = new HashMap<>(); 
		BigDecimal changeCarry = new BigDecimal(0);
		Map<String, BigDecimal> pointResult = new HashMap<>();
		
		for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
			BigDecimal change;
			BigDecimal payedForTransaction = transaction.getValue().getValue();
			
        	if (transaction.getType() == TransactionType.ORDER) {
        		continue;
        	} else {
	        	String transactionDate = format.format(transaction.getCreated().getTime());
	        	
	        	if (transaction.getType() == TransactionType.PAYOUT) {
	        		payedForTransaction = payedForTransaction.negate();
	        	}
	        	if (changeCarryMap.containsKey(transactionDate)) {
	    			BigDecimal old = changeCarryMap.get(transactionDate);
	    			change = old.subtract(payedForTransaction);
	    			
	    		} else {
	    			change = payedForTransaction.negate();
	    		}
	        	changeCarry = changeCarry.add(change);
	        	//changeMap.put(transactionDate, change);
	        	changeCarryMap.put(transactionDate, changeCarry);
	        	pointResult.put(transactionDate, startCapital.add(changeCarry));
        	}
        }
		
		for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
			if (transaction.getType() != TransactionType.ORDER) {
        		continue;
			}
			
			BigDecimal change;
			BigDecimal payedForTransaction = transaction.getValue().getValue();
			
			OrderTransactionEntry ot = (OrderTransactionEntry)transaction;
			BigDecimal volume = new BigDecimal(ot.getOrder().getVolume());
			List<ValuePaperHistoryEntry> historyEntries = priceDataAccess.getValuePaperHistoryEntriesForPortfolioAfterDate(portfolio, transaction.getCreated());
			
			for (ValuePaperHistoryEntry he : historyEntries) {
				BigDecimal closingPrice = he.getClosingPrice();
				String historyDate = format.format(he.getDate().getTime());
				
				if (changeMap.containsKey(historyDate)) {
	    			BigDecimal old = changeMap.get(historyDate);
	    			change = old.add(closingPrice.multiply(volume).subtract(payedForTransaction)); 
	    		} else {
	    			change = closingPrice.multiply(volume).subtract(payedForTransaction);
	    		}
				changeMap.put(historyDate, change);
			}
		}
		
		pointResult.put(format.format(portfolio.getCreated().getTime()), startCapital);
		
		for (String date : changeMap.keySet()) {
			BigDecimal change = changeMap.get(date);
			//BigDecimal lastValue = startCapital.add(change);
			
			if (pointResult.containsKey(date)) {
				BigDecimal old = pointResult.get(date);
				pointResult.put(date, old.add(change));
			} else {
				Date transactionDate = null;
				try {
					transactionDate = format.parse(date);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Date latestCarryBeforeCurrentTransaction = null;
				for (String carryDate : changeCarryMap.keySet()) {
					Date currCarry = null;
					try {
						currCarry = format.parse(carryDate);
					} catch (ParseException e) {
						e.printStackTrace();
						return new HashMap<String, BigDecimal>();
					}
					if (currCarry.before(transactionDate) && latestCarryBeforeCurrentTransaction == null) {
						latestCarryBeforeCurrentTransaction = currCarry;
					} else if (currCarry.before(transactionDate) && currCarry.after(latestCarryBeforeCurrentTransaction) ) {
						latestCarryBeforeCurrentTransaction = currCarry;
					}
				}
				if (latestCarryBeforeCurrentTransaction != null) {
					change = changeCarryMap.get(format.format(latestCarryBeforeCurrentTransaction)).add(change);
				}
				pointResult.put(date, startCapital.add(change));
			}	
			
			
		}
        
        return pointResult;
	}
	

	public List<NewsItem> getNewsForPortfolio(Portfolio portfolio) {
		List<NewsItem> news = new ArrayList<>();
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			ValuePaper valuePaper = pvp.getValuePaper();
			List<NewsItem> tmpNews = newsItemDataAccess.getNewsItemsByValuePaperCode(valuePaper.getCode());
			news.addAll(tmpNews);
		}
		return news;
	}
	
	public List<AnalystOpinion> getAnalystOpinionsForPortfolio(Portfolio portfolio) {
		List<AnalystOpinion> opinions = new ArrayList<>();
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			ValuePaper valuePaper = pvp.getValuePaper();
			List<AnalystOpinion> tmpOpinions = analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode(valuePaper.getCode());
			opinions.addAll(tmpOpinions);
		}
		return opinions;
	}

	public double getChange(PortfolioValuePaper pvp) {
		
		double payed = getTotalPayedForValuePaper(pvp.getValuePaper().getCode()).doubleValue();
		int volume = pvp.getVolume();
		double latestPrice = priceDataAccess.getLastPriceEntry(pvp.getValuePaper().getCode()).getPrice().doubleValue();
		
		return (latestPrice*volume - payed) * 100 / payed;
	}
	
	public double getProfit(PortfolioValuePaper pvp) {
		double payed = getTotalPayedForValuePaper(pvp.getValuePaper().getCode()).doubleValue();
		int volume = pvp.getVolume();
		double latestPrice = priceDataAccess.getLastPriceEntry(pvp.getValuePaper().getCode()).getPrice().doubleValue();
		
		return latestPrice*volume - payed;
	}

	public List<Portfolio> getActiveUserPortfolios(User user) {
		try{
			List<Portfolio> portfolios = em.createQuery("FROM Portfolio p LEFT JOIN FETCH p.game JOIN FETCH p.owner WHERE p.owner = :user", Portfolio.class).setParameter("user", user).getResultList();
			List<Portfolio> result = new ArrayList<>();
			for (Portfolio p : portfolios) {
				if (p.getGame() == null) {
					result.add(p);
					continue;
				}
				Calendar now = Calendar.getInstance();
				if (p.getGame().getValidTo().after(now)) {
					result.add(p);
				}
			}
			return result;
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
