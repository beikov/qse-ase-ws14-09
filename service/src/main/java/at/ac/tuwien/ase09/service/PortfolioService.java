package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.data.NewsItemDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.TransactionEntryDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.transaction.OrderTransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;
import at.ac.tuwien.ase09.model.transaction.TransactionType;

@Stateless
public class PortfolioService {

	@Inject
	private EntityManager em;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private ValuePaperPriceEntryDataAccess priceDataAccess;
	
	@Inject
	private NewsItemDataAccess newsItemDataAccess;
	
	@Inject
	private AnalystOpinionDataAccess analystOpinionDataAccess;
	
	@Inject
	private TransactionEntryDataAccess transactionDataAccess;
	
	public Map<ValuePaperType, Integer> getValuePaperTypeCountMap(Portfolio portfolio) {
		Map<ValuePaperType, Integer> valuePaperTypeCounterMap = new HashMap<ValuePaperType, Integer>();
		Iterator<PortfolioValuePaper> iter = portfolio.getValuePapers().iterator(); 
		while (iter.hasNext()) {
			PortfolioValuePaper association = iter.next();
			ValuePaper paper = association.getValuePaper();
			ValuePaperType type = paper.getType();
			int current = 0;
			if (valuePaperTypeCounterMap.get(type) != null)
				current = valuePaperTypeCounterMap.get(type);
			valuePaperTypeCounterMap.put(type, ++current);
		}
		return valuePaperTypeCounterMap;
	}
	
	public Map<String, Integer> getValuePaperCountryCountMap(Portfolio portfolio) {
		Map<String, Integer> valuePaperCountryCountMap = new HashMap<String, Integer>();
		Iterator<PortfolioValuePaper> iter = portfolio.getValuePapers().iterator(); 
		while (iter.hasNext()) {
			PortfolioValuePaper portfolioValuePaper = iter.next();
			ValuePaper paper = portfolioValuePaper.getValuePaper();
			if (!(paper instanceof Stock)) {
				continue;
			}
			Stock stock = (Stock)paper;
			
			String country = stock.getCountry();
			//String country = paper.getIsin().substring(0, 2);
			int current = 0;
			if (valuePaperCountryCountMap.get(country) != null)
				current = valuePaperCountryCountMap.get(country);
			valuePaperCountryCountMap.put(country, ++current);
		}
		return valuePaperCountryCountMap;
	}
	
	public void savePortfolio(Portfolio portfolio){
		em.persist(portfolio);
	}
	
	public Portfolio updatePortfolio(Portfolio portfolio){
		return em.merge(portfolio);
	}

	public boolean existsPortfolioWithNameForUser(String portfolioName, User user) {
		return portfolioDataAccess.getPortfolioByNameForUser(portfolioName, user) != null;
	}

	public BigDecimal getTotalPayedForValuePaper(String code) {
		
		List<OrderTransactionEntry> orderTransactions = transactionDataAccess.getOrderTransactionsForValuePaper(code);
		
		BigDecimal payed = new BigDecimal(0);
		for (OrderTransactionEntry ot : orderTransactions) {
			payed = payed.add(ot.getValue().getValue());
		}
		return payed;
	}
	
	/*public Money getTotalPayedPortfolioValuePaper(Portfolio portfolio, String code) {
		Money money = portfolio.getCurrentCapital();
		money.setValue(new BigDecimal(0));
		for (TransactionEntry t : portfolio.getTransactionEntries()) {
			if (t.getType() == TransactionType.ORDER && ((OrderTransactionEntry)t).getOrder().getValuePaper().getCode().equals(code)) {
				BigDecimal oldVal = money.getValue();
				BigDecimal newVal = oldVal.add(t.getValue().getValue());
				money.setValue(newVal);
			}
		}
		return money;
	}*/

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
	

	public List<NewsItem> getNewsForValuePapers(Set<PortfolioValuePaper> valuePapers) {
		List<NewsItem> news = new ArrayList<>();
		for (PortfolioValuePaper pvp : valuePapers) {
			ValuePaper valuePaper = pvp.getValuePaper();
			List<NewsItem> tmpNews = newsItemDataAccess.getNewsItemsByValuePaperCode(valuePaper.getCode());
			news.addAll(tmpNews);
		}
		return news;
	}
	
	public List<AnalystOpinion> getAnalystOpinionsForValuePapers(Set<PortfolioValuePaper> valuePapers) {
		List<AnalystOpinion> opinions = new ArrayList<>();
		for (PortfolioValuePaper pvp : valuePapers) {
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
}
