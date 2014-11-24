package at.ac.tuwien.ase09.test.persistence;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.BaseEntity;

@Stateless
public class DataManager {
	@Inject
	private EntityManager em;
	
    public <I extends Serializable, E extends BaseEntity<I>> void persist(final E entity) {
        em.persist(entity);
        em.flush();
    }
    
    public <I extends Serializable, E extends BaseEntity<I>> E detach(final E entity) {
        em.detach(entity);
        return entity;
    }
}
