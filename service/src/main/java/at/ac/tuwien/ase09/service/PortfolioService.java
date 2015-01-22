package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Stateless
public class PortfolioService extends AbstractService {
	
	public Portfolio createPortfolio(Portfolio portfolio) {
		portfolio.setOwner(em.getReference(User.class, userContext.getUser()));
		em.persist(portfolio);
		return portfolio;
	}
	
	public Portfolio updatePortfolio(Portfolio portfolio){
		return em.merge(portfolio);
	}

	public void removePortfolio(Portfolio portfolio) {
		em.remove(em.contains(portfolio) ? portfolio : em.merge(portfolio));
	}
	
}
