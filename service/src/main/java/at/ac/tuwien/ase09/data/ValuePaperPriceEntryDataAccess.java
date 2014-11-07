package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Stateless
public class ValuePaperPriceEntryDataAccess {

	@PersistenceContext
	private EntityManager em;
	
	public ValuePaperPriceEntry getLastPriceEntry(String code){
		try{
			return em.createQuery("SELECT price FROM ValuePaperPriceEntry price JOIN price.valuePaper vp WHERE vp.code=:code", ValuePaperPriceEntry.class)
				.setParameter("code", code)
				.getSingleResult();
		}catch(NoResultException e){
			throw new EntityNotFoundException(e);
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
