package at.ac.tuwien.ase09.data;

import java.util.Properties;

import javax.batch.api.BatchProperty;
import javax.batch.api.listener.JobListener;
import javax.batch.runtime.BatchRuntime;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("SuccessorJobStarter")
public class SuccessorJobStarter implements JobListener {
	@Inject
	@BatchProperty(name="successorJobIds")
	private String successorJobIds;
	
	@Override
	public void beforeJob() throws Exception { }

	@Override
	public void afterJob() throws Exception {
		if(successorJobIds != null){
			String[] ids = successorJobIds.split(";");
			StringBuilder remainingIds = new StringBuilder();
			if(ids.length > 1){
				remainingIds.append(ids[1]);
			}
			for(int i = 2; i < ids.length; i++){
				remainingIds.append(";");
				remainingIds.append(ids[i]);
			}
			Properties props = null;
			if(remainingIds.length() > 0){
				props = new Properties();
				props.put("successorJobIds", remainingIds.toString());
			}
			BatchRuntime.getJobOperator().start(ids[0], props);
		}
	}

}
