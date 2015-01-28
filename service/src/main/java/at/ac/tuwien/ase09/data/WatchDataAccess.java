package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Watch;

@Stateless
public class WatchDataAccess {

	@Inject
	private EntityManager em;

	public List<Watch> getWatches() {
		try {
			return em.createQuery("FROM Watch w JOIN FETCH w.valuePaper WHERE w.deleted = false", Watch.class).getResultList();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public Watch getWatch(Long id) {
		try {
			return em.createQuery("FROM Watch w JOIN FETCH w.valuePaper WHERE w.id = :id AND w.deleted = false", Watch.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
