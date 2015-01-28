package at.ac.tuwien.ase09.cep;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.WatchDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.event.Deleted;
import at.ac.tuwien.ase09.event.Updated;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Watch;
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;
import at.ac.tuwien.ase09.parser.PWatchCompiler;
import at.ac.tuwien.ase09.service.NotificationService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class WatchSingleton {

	private final ConcurrentMap<Long, EPStatement> statements = new ConcurrentHashMap<>();
	@Inject
	private EPServiceProvider epService;
	@Resource
	private ManagedExecutorService executorService;
	@Inject
	private NotificationService notificationService;
	@Inject
	private WatchDataAccess watchDataAccess;
	
	@PostConstruct
	void init() {
		for (Watch w : watchDataAccess.getWatches()) {
			addWatch(w);
		}
	}
	
	public void onWatchAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added Watch watch) {
		addWatch(watch);
	}
	
	public void onWatchUpdated(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Updated Watch watch) {
		removeWatch(watch.getId());
		addWatch(watch);
	}
	
	public void onWatchDeleted(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Deleted Watch watch) {
		removeWatch(watch.getId());
	}

	public void removeWatch(Long watchId) {
		if (watchId == null) {
			throw new NullPointerException("watchId");
		}
		
		EPStatement watchStatement = statements.remove(watchId);
		
		if (watchStatement == null) {
			throw new AppException("Illegal concurrent attempt to remove the already removed watch statement with the id: " + watchId);
		}
		
		watchStatement.destroy();
	}
	
	public void addWatch(Watch watch) {
		if (statements.containsKey(watch.getId())) {
			throw new AppException("A watch statement for the id '" + watch.getId() + "' already exists!");
		}
		
		String eplExpression = PWatchCompiler.compileEpl(watch.getExpression(), watch.getValuePaper().getId());
		EPStatement watchStatement = epService.getEPAdministrator().createEPL(eplExpression);
		
		if (statements.putIfAbsent(watch.getId(), watchStatement) != null) {
			throw new AppException("Illegal concurrent attempt to add the already existing watch statement with the id: " + watch.getId());
		}
		
		watchStatement.addListener(new WatchFireListener(executorService, notificationService, watch));
		watchStatement.start();
	}
	
	private static class WatchFireListener implements UpdateListener {
		
		private final ManagedExecutorService executorService;
		private final NotificationService notificationService;
		private final Watch watch;

		public WatchFireListener(ManagedExecutorService executorService, NotificationService notificationService, Watch watch) {
			this.executorService = executorService;
			this.notificationService = notificationService;
			this.watch = watch;
		}

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			final int length = newEvents.length;
			executorService.submit(() -> {
				for (int i = 0; i < length; i++) {
					WatchTriggeredNotification n = new WatchTriggeredNotification();
					n.setCreated(Calendar.getInstance());
					n.setUser(watch.getOwner());
					n.setWatch(watch);
					notificationService.addNotification(n);
				}
			});
		}
	}
}
