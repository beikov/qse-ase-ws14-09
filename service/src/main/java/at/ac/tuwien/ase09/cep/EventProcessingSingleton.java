package at.ac.tuwien.ase09.cep;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Produces;

import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.event.Constants;
import at.ac.tuwien.ase09.model.event.StockDTO;
import at.ac.tuwien.ase09.model.event.ValuePaperPriceEntryDTO;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class EventProcessingSingleton {

	private static final boolean DEBUG = true;
	private EPServiceProvider epService;
	
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
	}
	
	@PreDestroy
	void close() {
		epService.destroy();
	}
	
	@Produces
	@ApplicationScoped
	public EPServiceProvider getEPServiceProvider() {
		return epService;
	}

	public void onPriceEntryAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added ValuePaperPriceEntryDTO event) {
		epService.getEPRuntime().sendEvent(event);
	}

	public void onStockUpdated(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added StockDTO event) {
		epService.getEPRuntime().sendEvent(event);
	}
}
