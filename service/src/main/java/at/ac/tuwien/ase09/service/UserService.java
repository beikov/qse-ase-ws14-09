package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.User;

@Stateless
public class UserService {
	@PersistenceContext
	private EntityManager em;
	
	public void saveUser(User user){
		em.persist(user);
	}
}
