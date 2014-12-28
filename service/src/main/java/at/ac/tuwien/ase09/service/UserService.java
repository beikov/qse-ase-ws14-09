package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.User;

@Stateless
public class UserService {
	@Inject
	private EntityManager em;
	
	public void saveUser(User user){
		em.persist(user);
	}
	
	public void updateUser(User user) {
		em.merge(user);
	}
}
