package at.ac.tuwien.ase09.data.bean;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.User;

@Singleton
@Startup
public class TestSingleton {
	@PersistenceContext
	private EntityManager em;
	
	@PostConstruct
	public void init(){
		User u = new User();
		em.persist(u);
	}
}
