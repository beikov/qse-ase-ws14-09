package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;

@Stateless
public class ValuePaperDataAccess {
	@Inject
	private EntityManager em;

	public <T extends ValuePaper> T getValuePaperByCode(String code, Class<T> clazz){
		try{
			ValuePaper p = em.createQuery("SELECT v FROM " + clazz.getSimpleName() + " v WHERE v.code = :code", clazz).setParameter("code", code).getSingleResult();
		
			if(p.getType() == ValuePaperType.BOND){
				if(((StockBond)p).getBaseStock() != null)
				em.createQuery("SELECT b.baseStock FROM StockBond b WHERE b.code = :code", clazz).setParameter("code", code).getSingleResult();
			}
			
			return (T) p;
			
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
	
	public List<String> getStockCodesByIndex(String indexName){
		try{
			return em.createQuery("SELECT stock.code FROM Stock stock WHERE stock.index = :index", String.class).setParameter("index", indexName).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
