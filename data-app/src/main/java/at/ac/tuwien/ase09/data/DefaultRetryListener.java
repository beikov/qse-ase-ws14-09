package at.ac.tuwien.ase09.data;

import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("DefaultRetryListener")
public class DefaultRetryListener implements RetryProcessListener, RetryReadListener, RetryWriteListener{
	private static final Logger LOG = Logger.getLogger(DefaultRetryListener.class.getName());

	@Override
	public void onRetryWriteException(List<Object> items, Exception ex)
			throws Exception {
		LOG.warning("Retry chunk due to exception while writing " + items.size() + " items: " + ex.toString());
		
	}

	@Override
	public void onRetryReadException(Exception ex) throws Exception {
		LOG.warning("Retry chunk due to exception while reading item: " + ex.toString());
		
	}

	@Override
	public void onRetryProcessException(Object item, Exception ex)
			throws Exception {
		LOG.warning("Retry chunk due to exception while processing item " + item + ": " + ex.toString());
	}

}
