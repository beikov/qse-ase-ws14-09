package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.StockMarketGame;

@Stateless
public class StockMarketGameService {

	@Inject
	private EntityManager em;
	
	public StockMarketGame saveStockMarketGame(StockMarketGame stockMarketGame){
		if(stockMarketGame.getId() != null){
			if(em.find(StockMarketGame.class, stockMarketGame.getId()) != null){
				return em.merge(stockMarketGame);
			}
		}
		em.persist(stockMarketGame);
		return stockMarketGame;	
	}
	
}
