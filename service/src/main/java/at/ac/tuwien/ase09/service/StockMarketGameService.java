package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.notification.NotificationSingleton;

@Stateless
public class StockMarketGameService extends AbstractService {
	
	@Inject
	NotificationSingleton notificationSingleton;
	
	public StockMarketGame saveStockMarketGame(StockMarketGame stockMarketGame){
		if(stockMarketGame.getId() != null){
			if(em.find(StockMarketGame.class, stockMarketGame.getId()) != null){
				StockMarketGame ret = em.merge(stockMarketGame);
				notificationSingleton.addGame(ret);
				return ret;
			}
		}
		em.persist(stockMarketGame);
		notificationSingleton.addGame(stockMarketGame);
		return stockMarketGame;	
	}

	public void participateInGame(StockMarketGame game, Long userId) {
		// check if already done
		
		Portfolio p = new Portfolio();
		p.setName(game.getName() + "_Portfolio");
		p.setCreated(game.getValidFrom());
		p.setCurrentCapital(game.getSetting().getStartCapital());
		p.setGame(game);
		p.setOwner(em.getReference(User.class, userId));
		p.setSetting(game.getSetting());
		
		em.persist(p);
	}
	
}
