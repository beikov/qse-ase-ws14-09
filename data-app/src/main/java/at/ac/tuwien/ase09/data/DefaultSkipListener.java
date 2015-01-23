package at.ac.tuwien.ase09.data;

import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("DefaultSkipListener")
public class DefaultSkipListener implements SkipProcessListener, SkipReadListener, SkipWriteListener{
	private static final Logger LOG = Logger.getLogger(DefaultSkipListener.class.getName());
	@Override
	public void onSkipWriteItem(List<Object> items, Exception ex)
			throws Exception {
		LOG.warning("Skip write " + items.size() + " items due to " + ex.toString());
	}

	@Override
	public void onSkipReadItem(Exception ex) throws Exception {
		LOG.warning("Skip read due to " + ex.toString());
	}

	@Override
	public void onSkipProcessItem(Object item, Exception ex) throws Exception {
		LOG.warning("Skip processing item " + item + " due to " + ex.toString());
	}

}
