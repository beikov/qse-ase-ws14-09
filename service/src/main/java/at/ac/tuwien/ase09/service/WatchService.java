package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.WatchDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.event.Deleted;
import at.ac.tuwien.ase09.event.Updated;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.Watch;

@Stateless
public class WatchService extends AbstractService {
	
	@Inject
	@Added
	private Event<Watch> watchAdded;
	@Inject
	@Updated
	private Event<Watch> watchUpdated;
	@Inject
	@Deleted
	private Event<Watch> watchDeleted;
	@Inject
	private WatchDataAccess watchDataAccess;
	
	public Watch addWatch(Watch watch) {
		watch.setOwner(em.getReference(User.class, userContext.getUserId()));
		em.persist(watch);
		em.flush();
		watch = watchDataAccess.getWatch(watch.getId());
		watchAdded.fire(watch);
		return watch;
	}
	
	public Watch updateWatch(Watch watch) {
		watch = em.merge(watch);
		em.flush();
		watch = watchDataAccess.getWatch(watch.getId());
		watchUpdated.fire(watch);
		return watch;
	}
	
	public void removeWatch(Watch watch) {
		Watch w = em.getReference(Watch.class, watch.getId());
//		w.setDeleted(true);
		em.merge(w);
		em.flush();
		watchDeleted.fire(watch);
	}
}
