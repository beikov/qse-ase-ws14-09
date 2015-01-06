package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Portfolio;

@Stateless
public class PortfolioService {
	@Inject
	private EntityManager em;
	
	public Portfolio savePortfolio(Portfolio portfolio){
		if(portfolio.getId() != null){
			if(em.find(Portfolio.class, portfolio.getId()) != null){
				return em.merge(portfolio);
			}
		}
		em.persist(portfolio);
		return portfolio;
	}

	public void removePortfolio(Portfolio portfolio) {
		em.remove(em.contains(portfolio) ? portfolio : em.merge(portfolio));
	}
	
}
