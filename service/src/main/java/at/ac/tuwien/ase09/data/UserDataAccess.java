package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.User;

@Stateless
public class UserDataAccess {
	@Inject
	private EntityManager em;

	public User getByUsername(String username) {
		try {
			return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
