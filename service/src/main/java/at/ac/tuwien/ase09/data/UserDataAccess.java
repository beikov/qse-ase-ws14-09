package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.User;

@Stateless
public class UserDataAccess {
	@Inject
	private EntityManager em;

	public User getUserByUsername(String username) {
		try {
			return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
