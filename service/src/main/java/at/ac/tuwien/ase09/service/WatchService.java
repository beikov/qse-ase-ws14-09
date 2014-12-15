package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.espertech.esper.client.EPServiceProvider;

import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.model.Watch;

@Stateless
public class WatchService {
	
	@Inject
	private EntityManager em;
	@Inject
	private EPServiceProvider epService;
	
	@Inject
	@Added
	private Event<Watch> watchAdded;
	
	public void saveWatch(Watch watch) {
		em.persist(watch);
		em.flush();
		watchAdded.fire(watch);
	}
	
	public void onWatchAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added Watch watch) {
		epService.
	}
}
