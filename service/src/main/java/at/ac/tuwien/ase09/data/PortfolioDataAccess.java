package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;

@Stateless
public class PortfolioDataAccess {

	@PersistenceContext
	private EntityManager em;
	
	public List<Portfolio> getPortfolios() {
		try{
			return em.createQuery("FROM Portfolio", Portfolio.class).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public Portfolio getPortfolioById(Long id) {
		try {
			return em.createQuery("FROM Portfolio p JOIN FETCH p.valuePapers JOIN FETCH p.transactionEntries JOIN FETCH p.orders WHERE p.id = :id", Portfolio.class).setParameter("id", id).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
}
