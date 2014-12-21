package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;

@Stateless
public class StockMarketGameDataAccess {

	@Inject
	private EntityManager em;

	public  StockMarketGame getStockMarketGameByID(Long id){
		try{

			return em.createQuery("Select s FROM StockMarketGame s "
					+ "LEFT JOIN FETCH s.allowedValuePapers"
					+ "JOIN FETCH s.owner o"
					+ "JOIN FETCH o.admin "
					+ "WHERE s.id = :id", StockMarketGame.class).setParameter("id", id).getSingleResult();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}

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
