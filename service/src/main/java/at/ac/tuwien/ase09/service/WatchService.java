package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.cep.EventProcessingSingleton;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.event.Updated;
import at.ac.tuwien.ase09.model.Watch;

@Stateless
public class WatchService {
	
	@Inject
	private EntityManager em;
	
	@Inject
	@Added
	private Event<Watch> watchAdded;
	@Inject
	@Updated
	private Event<Watch> watchUpdated;
	@Inject
	private EventProcessingSingleton epService;
	
	public Watch addWatch(Watch watch) {
		em.persist(watch);
		em.flush();
		watchAdded.fire(watch);
		return watch;
	}
	
	public Watch updateWatch(Watch watch) {
		watch = em.merge(watch);
		em.flush();
		watchUpdated.fire(watch);
		return watch;
	}
	
	public void onWatchAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added Watch watch) {
		epService.addWatch(watch);
	}
	
	public void onWatchUpdated(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Updated Watch watch) {
		epService.removeWatch(watch.getId());
		epService.addWatch(watch);
	}
}
