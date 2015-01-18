package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Logo;

@Stateless
public class LogoDataAccess {
	
	@Inject
	private EntityManager em;

	public Logo getByClassAndId(Class<Logo> entityClass, Long id) {
		
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
		    CriteriaQuery<Logo> criteria = builder.createQuery(entityClass);
		    Root<Logo> entityRoot = criteria.from(entityClass);
		    criteria.select(entityRoot);
		    criteria.where(builder.equal(entityRoot.get("id"), id));
		    return em.createQuery(criteria).getSingleResult();
		} catch(NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch(Exception e) {
			throw new AppException(e);
		}
	}
}
