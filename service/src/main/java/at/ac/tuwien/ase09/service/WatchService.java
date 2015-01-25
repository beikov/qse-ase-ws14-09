package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.cep.EventProcessingSingleton;
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
	private EventProcessingSingleton epService;
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
		em.remove(em.getReference(Watch.class, watch.getId()));
		em.flush();
		watchDeleted.fire(watch);
	}
	
	public void onWatchAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added Watch watch) {
		epService.addWatch(watch);
	}
	
	public void onWatchUpdated(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Updated Watch watch) {
		epService.removeWatch(watch.getId());
		epService.addWatch(watch);
	}
	
	public void onWatchDeleted(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Deleted Watch watch) {
		epService.removeWatch(watch.getId());
	}
}
