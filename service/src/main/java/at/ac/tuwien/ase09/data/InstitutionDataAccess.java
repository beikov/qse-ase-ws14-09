package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.User;

@Stateless
public class InstitutionDataAccess {
	@Inject
	private EntityManager em;

	public Institution getById(Long id) {
		try {
			return em.createQuery("FROM Institution i WHERE i.id = :id", Institution.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
	public Institution getByAdmin(String username) {
		try {
			return em.createQuery("SELECT i FROM Institution i join fetch i.admin WHERE i.admin.username = :username", Institution.class).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
