package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;

@Stateless
public class ValuePaperDataAccess {
	@PersistenceContext
	private EntityManager em;
	
	public Stock getStockByIsin(String isin){
		try{
			return em.createQuery("SELECT s FROM Stock s WHERE s.isin = :isin", Stock.class).setParameter("isin", isin).getSingleResult();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public List<Stock> getStocksByIndex(String indexName){
		try{
			return em.createQuery("SELECT s FROM Stock s WHERE s.index = :index", Stock.class).setParameter("index", indexName).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
