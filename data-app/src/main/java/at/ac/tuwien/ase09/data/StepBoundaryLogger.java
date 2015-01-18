package at.ac.tuwien.ase09.data;

import java.util.logging.Logger;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("StepBoundaryLogger")
public class StepBoundaryLogger implements StepListener {
	private static final Logger LOG = Logger.getLogger(StepBoundaryLogger.class.getName());

	@Inject
	private StepContext stepContext;

	@Override
	public void beforeStep() throws Exception {
		LOG.info("Starting step " + stepContext.getStepName());
		
	}

	@Override
	public void afterStep() throws Exception {
		LOG.info("Step " + stepContext.getStepName() + " finished");
	}
	
}
