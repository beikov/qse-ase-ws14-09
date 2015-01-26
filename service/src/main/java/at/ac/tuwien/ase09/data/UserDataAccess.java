package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Stateless
public class UserDataAccess {
	@Inject
	private EntityManager em;

	// TODO: Cache
	public User getUserById(Long userId) {
		try {
			return em.createQuery("SELECT u FROM User u WHERE u.id = :userId", User.class).setParameter("userId", userId).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	// TODO: Cache
	public User getUserByUsername(String username) {
		try {
			return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}


	public User loadUserForProfile(String username) {
		try {
			return em.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.followers WHERE u.username = :username", User.class).setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
	public String getEmailByUsername(String username){
		try {
			return (String) em.createNativeQuery("SELECT ue.email FROM user_entity ue WHERE ue.username = :username").setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
