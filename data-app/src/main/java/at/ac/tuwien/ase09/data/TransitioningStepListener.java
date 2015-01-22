package at.ac.tuwien.ase09.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("TransitioningStepListener")
public class TransitioningStepListener implements StepListener {
	@Inject
	private JobContext jobContext;

	@Inject
	private StepContext stepContext;

	@Override
	public void beforeStep() throws Exception {
		stepContext.setPersistentUserData((Serializable) jobContext.getTransientUserData());

	}

	@Override
	public void afterStep() throws Exception {	}
}
