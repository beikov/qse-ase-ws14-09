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
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;

@Stateless
public class PortfolioService {

	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	public Map<ValuePaperType, Integer> getValuePaperTypeCountMap(Portfolio portfolio) {
		Map<ValuePaperType, Integer> valuePaperTypeCounterMap = new HashMap<ValuePaperType, Integer>();
		Iterator<ValuePaper> iter = portfolio.getValuePapers().iterator(); 
		while (iter.hasNext()) {
			ValuePaper paper = iter.next();
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
		Iterator<ValuePaper> iter = portfolio.getValuePapers().iterator(); 
		while (iter.hasNext()) {
			ValuePaper paper = iter.next();
			String country = paper.getCountry();
			int current = 0;
			if (valuePaperCountryCountMap.get(country) != null)
				current = valuePaperCountryCountMap.get(country);
			valuePaperCountryCountMap.put(country, ++current);
		}
		return valuePaperCountryCountMap;
	}
}
