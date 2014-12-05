package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
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
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Inject
	private ValuePaperPriceEntryDataAccess priceDataAccess;
	
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
	
	public void updatePortfolio(Portfolio portfolio){
		em.merge(portfolio);
	}

	public boolean existsPortfolioWithNameForUser(String portfolioName, User user) {
		return portfolioDataAccess.getPortfolioByNameForUser(portfolioName, user) != null;
	}

	public Money getTotalPayedPortfolioValuePaper(Portfolio portfolio, String code) {
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
	}

	public Map<String, BigDecimal> getPortfolioChartEntries(Portfolio portfolio) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String creationDate = format.format(portfolio.getCreated().getTime());
        BigDecimal startCapital = portfolio.getSetting().getStartCapital().getValue();
		Map<String, BigDecimal> pointResult = new HashMap<>();
		
		pointResult.put(creationDate, startCapital);
		
		for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
        	if (transaction.getType() == TransactionType.ORDER) {
        		continue;
        	}
        	
        	String date = format.format(transaction.getCreated().getTime());
        	BigDecimal value = transaction.getValue().getValue();
        	
        	if (transaction.getType() == TransactionType.PAYOUT) {
        		value = value.negate();
        	}
        	
        	if (pointResult.containsKey(date)) {
    			BigDecimal old = pointResult.get(date);
    			pointResult.put(date, old.subtract(value));
    		} else {
    			pointResult.put(date, value.negate());
    		}
        }
		
		List<ValuePaperHistoryEntry> historyEntries = priceDataAccess.getHistoricValuePaperPricesByPortfolioId(portfolio.getId());
        for (ValuePaperHistoryEntry historyEntry : historyEntries) {
        	Calendar calendar = historyEntry.getDate();
        	calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        	BigDecimal totalBuyPrice = new BigDecimal(0);
        	BigDecimal totalValue = new BigDecimal(0);
        	
        	for (TransactionEntry transaction : portfolio.getTransactionEntries()) {
        		if (transaction.getType() == TransactionType.ORDER && transaction.getCreated().before(calendar)) {        				
        			OrderTransactionEntry ot = (OrderTransactionEntry)transaction;
        			totalBuyPrice = totalBuyPrice.add(ot.getValue().getValue());
            		BigDecimal volume = BigDecimal.valueOf(ot.getOrder().getVolume());
            		totalValue = totalValue.add(volume.multiply(historyEntry.getClosingPrice()));
            		//totalValue = totalValue.add(historyEntry.getClosingPrice());
        		}
        	}
        	
        	String date = format.format(calendar.getTime());
        	BigDecimal pointValue = startCapital.subtract(totalBuyPrice).add(totalValue);
        	if (pointResult.containsKey(date)) {
        		BigDecimal oldValue = pointResult.get(date);
        		pointResult.put(format.format(calendar.getTime()), oldValue.add(pointValue));
        	} else {
        		pointResult.put(date, pointValue);
        	}
        }
        
        return pointResult;
	}

	public List<NewsItem> getNewsForValuePapers(Set<PortfolioValuePaper> valuePapers) {
		List<NewsItem> news = new ArrayList<>();
		for (PortfolioValuePaper pvp : valuePapers) {
			ValuePaper valuePaper = pvp.getValuePaper();
			List<NewsItem> tmpNews = valuePaperDataAccess.getNewsItemsForValuePaper(valuePaper);
			news.addAll(tmpNews);
		}
		return news;
	}
}
