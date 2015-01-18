package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.Join;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;

@Stateless
public class StockMarketGameDataAccess {

	@Inject
	private EntityManager em;
	
	
	public List<StockMarketGame> getStockMargetGames() {
		try{
			return em.createQuery("FROM StockMarketGame g JOIN FETCH g.owner", StockMarketGame.class).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public  StockMarketGame getStockMarketGameByID(Long id){
		try{

			return em.createQuery("Select s FROM StockMarketGame s "
					+ "LEFT JOIN FETCH s.allowedValuePapers "
					+ "JOIN FETCH s.owner o "
					+ "JOIN FETCH o.admin "
					+ "WHERE s.id = :id", StockMarketGame.class).setParameter("id", id).getSingleResult();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public List<StockMarketGame> findByNameTextOwner(String name, String text, String owner) {
		try {
			name = name == null ? "" : name;
			text = text == null ? "" : text;
			owner = owner == null ? "" : owner;
			
			return em.createQuery("FROM StockMarketGame g JOIN FETCH g.owner where g.name like :name and g.text like :text and g.owner.name like :owner", StockMarketGame.class).setParameter("name", "%"+name+"%").setParameter("text", "%"+text+"%").setParameter("owner", "%"+owner+"%").getResultList();
		} catch(Exception e) {
			throw new AppException(e);
		}
	}

}
