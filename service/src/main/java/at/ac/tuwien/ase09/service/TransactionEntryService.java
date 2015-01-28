package at.ac.tuwien.ase09.service;


import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerTransactionAddedNotification;
import at.ac.tuwien.ase09.model.transaction.TransactionEntry;

@Stateless
public class TransactionEntryService extends AbstractService {
	
	@Inject
	private NotificationService notificationService;
	@Resource
	private ManagedExecutorService managedExecutorService;
	@Inject
	private Instance<TransactionEntryService> self;

	public void onPriceEntryAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added TransactionEntry entry) {
		managedExecutorService.submit(() -> {
			self.get().notifyFollowers(entry);
		});
	}
	
	public void notifyFollowers(TransactionEntry entry) {
		Portfolio p = em.createQuery("SELECT p FROM Portfolio p LEFT JOIN FETCH p.followers JOIN FETCH p.owner o LEFT JOIN FETCH o.followers WHERE p.id = :portfolioId", Portfolio.class)
			.setParameter("portfolioId", entry.getPortfolio().getId())
			.getSingleResult();
		Set<User> receipents = new HashSet<>();
		receipents.addAll(p.getFollowers());
		receipents.addAll(p.getOwner().getFollowers());
		
		for (User u : receipents) {
			FollowerTransactionAddedNotification n = new FollowerTransactionAddedNotification();
			n.setTransactionEntry(entry);
			n.setUser(u);
			notificationService.addNotification(n);
		}
	}
}
