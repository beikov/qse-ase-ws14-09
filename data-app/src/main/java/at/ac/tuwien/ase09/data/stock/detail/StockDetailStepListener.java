package at.ac.tuwien.ase09.data.stock.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.batch.api.listener.StepListener;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("StockDetailStepListener")
public class StockDetailStepListener implements StepListener {

	@Inject
	private JobContext jobContext;

	@Inject
	private StepContext stepContext;

	@Override
	public void beforeStep() throws Exception {
		stepContext.setPersistentUserData((ArrayList<String>) 
						(List<String>) jobContext.getTransientUserData());

	}

	@Override
	public void afterStep() throws Exception {
	}

}
