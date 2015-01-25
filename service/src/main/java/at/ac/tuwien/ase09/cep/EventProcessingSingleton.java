package at.ac.tuwien.ase09.cep;

import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.WatchDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.Watch;
import at.ac.tuwien.ase09.model.event.Constants;
import at.ac.tuwien.ase09.model.event.StockDTO;
import at.ac.tuwien.ase09.model.event.ValuePaperPriceEntryDTO;
import at.ac.tuwien.ase09.model.notification.WatchTriggeredNotification;
import at.ac.tuwien.ase09.parser.PWatchCompiler;
import at.ac.tuwien.ase09.service.NotificationService;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EventProcessingSingleton {

	private static final boolean DEBUG = true;
	private final ConcurrentMap<Long, EPStatement> statements = new ConcurrentHashMap<>();
	private EPServiceProvider epService;
	
	@Inject
	private WatchDataAccess watchDataAccess;
	@Inject
	private NotificationService notificationService;
	@Resource
	private ManagedExecutorService executorService;
	
	@PostConstruct
	void init() {
		Configuration config = new Configuration();
		config.addEventType(Constants.VALUE_PAPER_PRICE_ENTRY, ValuePaperPriceEntryDTO.class);
		config.addEventType(Constants.STOCK, StockDTO.class);
		config.addPlugInSingleRowFunction("SIN", Math.class.getName(), "sin");
		config.addPlugInSingleRowFunction("COS", Math.class.getName(), "cos");
		config.addPlugInSingleRowFunction("TAN", Math.class.getName(), "tan");
		config.addPlugInSingleRowFunction("EXP", Math.class.getName(), "exp");
		config.addPlugInSingleRowFunction("LOG", Math.class.getName(), "log");
		config.addPlugInSingleRowFunction("POW", Math.class.getName(), "pow");
		config.addPlugInSingleRowFunction("SQRT", Math.class.getName(), "sqrt");

		if (DEBUG) {
			config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
			config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
			config.getEngineDefaults().getLogging().setEnableQueryPlan(false);
		}
		
		epService = EPServiceProviderManager.getDefaultProvider(config);
		
		for (Watch w : watchDataAccess.getWatches()) {
			addWatch(w);
		}
	}
	
	public void addEvent(ValuePaperPriceEntryDTO event) {
		epService.getEPRuntime().sendEvent(event);
	}
	
	public void addEvent(StockDTO event) {
		epService.getEPRuntime().sendEvent(event);
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
	
	@PreDestroy
	void close() {
		epService.destroy();
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
			for (int i = 0; i < newEvents.length; i++) {
				executorService.submit(() -> {
					WatchTriggeredNotification n = new WatchTriggeredNotification();
					n.setCreated(Calendar.getInstance());
					n.setUser(watch.getOwner());
					n.setWatch(watch);
					notificationService.addNotification(n);
				});
			}
		}
	}
}
