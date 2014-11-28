package at.ac.tuwien.ase09.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;

@Stateless
public class PortfolioService {

	@Inject
	private EntityManager em;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
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
}
