package at.ac.tuwien.ase09.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;

@Stateless
public class PortfolioService {

	@PersistenceContext
	private EntityManager em;
	
	
	public List<Portfolio> getPortfolios() {
		return em.createQuery("FROM Portfolio", Portfolio.class).getResultList();
	}
	
	public Portfolio getPortfolioById(Long id) {
		return em.createQuery("FROM Portfolio p JOIN FETCH p.valuePapers JOIN FETCH p.transactionEntries WHERE p.id = :id", Portfolio.class).setParameter("id", id).getSingleResult();
	}
	
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
