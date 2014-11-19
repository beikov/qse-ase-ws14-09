package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;

@Stateless
public class ValuePaperDataAccess {
	@PersistenceContext
	private EntityManager em;

	public <T extends ValuePaper> T getValuePaperByIsin(String isin, Class<T> clazz){
		try{
			return em.createQuery("SELECT v FROM " + clazz.getSimpleName() + " v WHERE v.isin = :isin", clazz).setParameter("isin", isin).getSingleResult();
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
	
	public List<StockBond> getStockBondsByBaseStockIndex(String indexName){
		try{
			return em.createQuery("SELECT bond FROM StockBond bond JOIN bond.baseStock baseStock WHERE baseStock.index = :index", StockBond.class).setParameter("index", indexName).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}

	public <T extends ValuePaper> List<T> getValuePapers(Class<T> clazz){
		try{
			return em.createQuery("SELECT v FROM " + clazz.getSimpleName() + " v", clazz).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
