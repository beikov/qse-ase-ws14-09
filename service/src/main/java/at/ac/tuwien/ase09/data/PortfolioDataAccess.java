package at.ac.tuwien.ase09.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.currency.CurrencyConversionService;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Money;
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
	
	@Inject
	private CurrencyConversionService currencyConversionService;
	
	public List<Portfolio> getPortfolios() {
		try{
			return em.createQuery("FROM Portfolio", Portfolio.class).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public List<Portfolio> getPortfoliosByUser(long userId) {
		try{
			User user = em.getReference(User.class, userId);
			return em.createQuery("FROM Portfolio p WHERE p.owner = :user and p.deleted=false", Portfolio.class).setParameter("user", user).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public List<Portfolio> getPortfoliosByStockMarketGame(long stockMarketGameId) {
		try{
			StockMarketGame smg = em.getReference(StockMarketGame.class, stockMarketGameId);
			return em.createQuery("SELECT p FROM Portfolio p LEFT JOIN FETCH p.owner WHERE p.game = :smg and p.deleted=false", Portfolio.class).setParameter("smg", smg).getResultList();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public BigDecimal getCostValueForPortfolio(long portfolioId) {
		try{
			List<PortfolioValuePaper> valuePapers = em.createQuery("from PortfolioValuePaper pvp join fetch pvp.portfolio where pvp.portfolio.id = :portfolioId", PortfolioValuePaper.class).setParameter("portfolioId", portfolioId).getResultList();
			if (valuePapers.isEmpty()) {
				return new BigDecimal(0);
			}
			Portfolio portfolio = valuePapers.get(0).getPortfolio();
			BigDecimal total = new BigDecimal(0);
			Currency portfolioCurrency = portfolio.getCurrentCapital().getCurrency();
			for (PortfolioValuePaper pvp : valuePapers) {
				BigDecimal value = pvp.getBuyPrice().multiply(new BigDecimal(pvp.getVolume()));
				ValuePaper vp = pvp.getValuePaper(); 
				Currency currency;
				if (vp instanceof Stock) {
					currency = ((Stock) vp).getCurrency();
				} else {
					currency = ((Fund) vp).getCurrency();
				}
				
				if (!portfolioCurrency.getCurrencyCode().equals(currency.getCurrencyCode())) {
					BigDecimal conversionRate = currencyConversionService.getConversionRate(currency, portfolioCurrency);
					value = CurrencyConversionService.convert(value, conversionRate);
				}
				total = total.add(value);
			}
			return total;
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public BigDecimal getCurrentValueForPortfolio(long portfolioId) {
		try{
			Portfolio portfolio = em.find(Portfolio.class, portfolioId);
			List<Object[]> valuePapersAndCurrentPrices = em.createQuery("SELECT vp, pe.price * pvp.volume FROM PortfolioValuePaper pvp, ValuePaperPriceEntry pe JOIN pe.valuePaper vp WHERE pvp.valuePaper = pe.valuePaper AND pvp.portfolio = :portfolio AND vp.class != 'BOND' AND pe.created >= ALL(SELECT pe2.created FROM ValuePaperPriceEntry pe2 WHERE pe2.valuePaper = pe.valuePaper)", Object[].class).setParameter("portfolio", portfolio).getResultList();
		
			List<Money> currentPrices = valuePapersAndCurrentPrices.stream().map(valuePaperAndCurrentPrice -> {
				ValuePaper vp = (ValuePaper) valuePaperAndCurrentPrice[0];
				Currency currency;
				if(vp instanceof Stock){
					currency = ((Stock) vp).getCurrency();
				}else{
					currency = ((Fund) vp).getCurrency();
				}
				return new Money((BigDecimal) valuePaperAndCurrentPrice[1], currency);
			}).collect(Collectors.toList());
			
			final Currency portfolioCurrency = portfolio.getCurrentCapital().getCurrency();
			
			// get currency conversion rates
			final Map<Currency, BigDecimal> currencyConversions = currentPrices.stream()
					.map(currentPrice -> currentPrice.getCurrency())
					.distinct()
					.filter(currency -> !currency.equals(portfolioCurrency))
					.collect(Collectors.toMap(currency -> currency, currency -> currencyConversionService.getConversionRate(currency, portfolioCurrency), (oldVal, newVal) -> newVal));
			
			// sum up
			return currentPrices.stream().reduce(new Money(new BigDecimal(0), portfolioCurrency), (sum, money) -> {
				BigDecimal conversionRate = currencyConversions.get(money.getCurrency());
				if(conversionRate != null){
					sum.setValue(sum.getValue().add(money.getValue().multiply(conversionRate)));
				}else{
					sum.setValue(sum.getValue().add(money.getValue()));
				}
				return sum;
			}).getValue();
			
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public BigDecimal getPortfolioPerformance(long portfolioId) {
		try{
			BigDecimal old = getCostValueForPortfolio(portfolioId);
			BigDecimal cur = getCurrentValueForPortfolio(portfolioId);
			return cur.subtract(old).multiply(new BigDecimal("100")).divide(old,4, RoundingMode.HALF_UP);
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
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
					+ "LEFT JOIN FETCH p.game "
					+ "JOIN FETCH p.owner "
					+ "LEFT JOIN FETCH p.followers "
					+ "LEFT JOIN FETCH p.game "
					+ "WHERE p.id = :id and p.deleted=false", Portfolio.class).setParameter("id", id).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}
	}
	
	/*public Portfolio getPortfolioByUsernameAndId(String username, Long id) {
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
	}*/
	
	public Portfolio getPortfolioByNameForUser(String portfolioName, Long userId){
		try{
			List<Portfolio> results;
			results = em.createQuery("FROM Portfolio p WHERE p.owner.id = :userId AND p.name = :name and p.deleted=false", Portfolio.class).setParameter("userId", userId).setParameter("name", portfolioName).getResultList();
			if (results.isEmpty()){
				return null;
			}else{
				return results.get(0);
			}
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public Portfolio getByGameAndUser(StockMarketGame game, Long userId) {
		try {
			return em.createQuery("FROM Portfolio p "
					+ "LEFT JOIN FETCH p.orders "
					+ "LEFT JOIN FETCH p.game "
					+ "JOIN FETCH p.owner "
					+ "WHERE p.game.id = :gameId AND p.owner.id = :ownerId and p.deleted=false", Portfolio.class).setParameter("gameId", game.getId()).setParameter("ownerId", userId).getSingleResult();
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
	
	public boolean existsPortfolioWithNameForUser(String portfolioName, Long userId) {
		return getPortfolioByNameForUser(portfolioName, userId) != null;
	}

	/*public BigDecimal getTotalPayedForPortfolioValuePaper(PortfolioValuePaper pvp) {
		
		List<OrderTransactionEntry> orderTransactions = transactionDataAccess.getOrderTransactionsForPortfolioValuePaper(pvp);
		
		BigDecimal payed = new BigDecimal(0);
		for (OrderTransactionEntry ot : orderTransactions) {
			payed = payed.add(ot.getValue().getValue());
		}
		return payed;
	}*/

	public Map<String, BigDecimal> getPortfolioChartEntries(Portfolio portfolio) {
		long startTime = System.currentTimeMillis();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		BigDecimal startCapital = portfolio.getSetting().getStartCapital().getValue();
		Map<String, BigDecimal> changeMap = new HashMap<>();
		Map<String, BigDecimal> changeCarryMap = new HashMap<>(); 
		BigDecimal changeCarry = new BigDecimal(0);
		Map<String, BigDecimal> pointResult = new HashMap<>();
		
		for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
			if (transaction.getType() == TransactionType.ORDER) {
				continue;
			}
			
			BigDecimal change;
			BigDecimal payedForTransaction = transaction.getValue().getValue();
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
		
		for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
			if (transaction.getType() != TransactionType.ORDER) {
        		continue;
			}
			
			OrderTransactionEntry ot = (OrderTransactionEntry)transaction;
			
			if (ot.getOrder().getValuePaper().getType() == ValuePaperType.BOND) {
				continue;
				// FIX: is currency set inside transaction for bond orderTransactions ?
			}
			
			BigDecimal change;
			BigDecimal payedForTransaction = transaction.getValue().getValue().multiply(new BigDecimal(ot.getOrder().getVolume()));
			
			BigDecimal volume = new BigDecimal(ot.getOrder().getVolume());
			List<ValuePaperHistoryEntry> historyEntries = priceDataAccess.getValuePaperHistoryEntriesForPortfolioAfterDate(portfolio, transaction.getCreated());
			
			Currency currency = transaction.getValue().getCurrency();
			Currency portfolioCurrency = transaction.getPortfolio().getCurrentCapital().getCurrency();
			BigDecimal conversionRate = null;
			if (!currency.getCurrencyCode().equals(portfolioCurrency.getCurrencyCode())) {
				conversionRate = currencyConversionService.getConversionRate(currency, portfolioCurrency);
			}
			
			for (ValuePaperHistoryEntry he : historyEntries) {
				BigDecimal closingPrice = he.getClosingPrice();
				String historyDate = format.format(he.getDate().getTime());
				
				if (conversionRate != null) {
					closingPrice = currencyConversionService.convert(closingPrice, conversionRate);
				}
				
				
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
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("################### "+ estimatedTime);
        return pointResult;
	}
	

	public List<NewsItem> getNewsForPortfolio(Portfolio portfolio) {
		List<NewsItem> news = new ArrayList<>();
		Set<String> keys = new HashSet<>();
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			if (keys.contains(pvp.getValuePaper().getName())) {
				continue;
			}
			keys.add(pvp.getValuePaper().getName());
			ValuePaper valuePaper = pvp.getValuePaper();
			List<NewsItem> tmpNews = newsItemDataAccess.getNewsItemsByValuePaperCode(valuePaper.getCode());
			news.addAll(tmpNews);
		}
		return news;
	}
	
	public List<AnalystOpinion> getAnalystOpinionsForPortfolio(Portfolio portfolio) {
		List<AnalystOpinion> items = new ArrayList<>();
		Set<String> keys = new HashSet<>();
		for (PortfolioValuePaper pvp : portfolio.getValuePapers()) {
			if (keys.contains(pvp.getValuePaper().getName())) {
				continue;
			}
			keys.add(pvp.getValuePaper().getName());
			ValuePaper valuePaper = pvp.getValuePaper();
			List<AnalystOpinion> tmpOpinions = analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode(valuePaper.getCode());
			items.addAll(tmpOpinions);
		}
		return items;
	}

	public double getChange(PortfolioValuePaper pvp) {
		//double payed = getTotalPayedForPortfolioValuePaper(pvp).doubleValue();
		int volume = pvp.getVolume();
		double payed = pvp.getBuyPrice().doubleValue()*volume;
		double latestPrice;
		try {
			latestPrice = priceDataAccess.getLatestPrice(pvp.getValuePaper().getCode()).doubleValue();
			return (latestPrice*volume - payed) * 100 / payed;
		} catch(Exception e) {
			return 0;
		}
	}
	
	public double getProfit(PortfolioValuePaper pvp) {
		//double payed = getTotalPayedForPortfolioValuePaper(pvp).doubleValue();
		int volume = pvp.getVolume();
		double payed = pvp.getBuyPrice().doubleValue()*volume;
		double latestPrice;
		try {
			latestPrice = priceDataAccess.getLatestPrice(pvp.getValuePaper().getCode()).doubleValue();
			return latestPrice*volume - payed;
		} catch(Exception e) {
			return 0;
		}
	}

	public List<Portfolio> getActiveUserPortfolios(Long userId) {
		try{
			return em.createQuery(
					"SELECT p "
					+ "FROM Portfolio p "
					+ "LEFT JOIN FETCH p.game game "
					+ "JOIN FETCH p.owner owner "
					+ "WHERE owner.id = :userId "
					+ "AND (game IS NULL OR game.validTo > :now) and p.deleted=false "
					+ "ORDER BY p.name", Portfolio.class)
				.setParameter("userId", userId)
				.setParameter("now", Calendar.getInstance())
				.getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
