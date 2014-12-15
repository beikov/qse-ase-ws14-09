package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Watch;

@Stateless
public class WatchDataAccess {

	@Inject
	private EntityManager em;

	public List<Watch> getWatches() {
		try {
			return em.createQuery("FROM Watch", Watch.class).getResultList();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
