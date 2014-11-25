package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

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
	
	public List<Portfolio> getPortfoliosByUser(User user) {
		try{
			return em.createQuery("FROM Portfolio p WHERE p.owner = :user", Portfolio.class).setParameter("user", user).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}
	}
	
	public Portfolio getPortfolioById(Long id) {
		try {
			return em.createQuery("FROM Portfolio p "
					+ "LEFT JOIN FETCH p.valuePapers "
					+ "LEFT JOIN FETCH p.transactionEntries "
					+ "LEFT JOIN FETCH p.orders o "
					+ "JOIN FETCH o.valuePaper "
					+ "JOIN FETCH p.owner "
					+ "LEFT JOIN FETCH p.followers "
					+ "WHERE p.id = :id", Portfolio.class).setParameter("id", id).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
	
	public Portfolio getPortfolioByNameForUser(String portfolioName, User user){
		try{
			List<Portfolio> results;
			results = em.createQuery("FROM Portfolio p WHERE p.owner = :user AND p.name = :name", Portfolio.class).setParameter("user", user).setParameter("name", portfolioName).getResultList();
			if (results.isEmpty()){
				return null;
			}else{
				return results.get(0);
			}
		}catch(Exception e){
			throw new AppException(e);
		}
	}
}
