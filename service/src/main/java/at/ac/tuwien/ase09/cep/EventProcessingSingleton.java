package at.ac.tuwien.ase09.cep;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

@Singleton
@Startup
public class EventProcessingSingleton {

	private static final boolean DEBUG = true;
	private EPServiceProvider epService;
	
	@PostConstruct
	void init() {
		Configuration config = new Configuration();
		config.addEventType(Constants.EVENT_TASK, TaskWrapperDTO.class);
		
		if (DEBUG) {
			config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
			config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
			config.getEngineDefaults().getLogging().setEnableQueryPlan(false);
		}
		
		epService = EPServiceProviderManager.getDefaultProvider(config);
	}
	
	@Produces
	public EPServiceProvider getEPService() {
		return epService;
	}
	
	@PreDestroy
	void close() {
		epService.destroy();
	}
}
