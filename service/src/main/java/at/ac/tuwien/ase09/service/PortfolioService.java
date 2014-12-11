package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Portfolio;

@Stateless
public class PortfolioService {
	@Inject
	private EntityManager em;
	
	public void savePortfolio(Portfolio portfolio){
		em.persist(portfolio);
	}
	
	public Portfolio updatePortfolio(Portfolio portfolio){
		return em.merge(portfolio);
	}
}
