package at.ac.tuwien.ase09.data;

import java.util.logging.Logger;

import javax.batch.api.listener.JobListener;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("JobBoundaryLogger")
public class JobBoundaryLogger implements JobListener{
	private static final Logger LOG = Logger.getLogger(StepBoundaryLogger.class.getName());
	
	@Inject
	private JobContext jobContext;
	
	@Override
	public void beforeJob() throws Exception {
		LOG.info("Starting job " + jobContext.getJobName());
	}

	@Override
	public void afterJob() throws Exception {
		LOG.info("Job " + jobContext.getJobName() + " finished");
	}

}
