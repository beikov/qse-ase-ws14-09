package at.ac.tuwien.ase09.data;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;

public abstract class AbstractEntityWriter<T> extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(AbstractEntityWriter.class.getName());
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for(int i = 0; i < items.size(); i++){
			Object entity = items.get(i);
			persistEntity((T) entity);
			LOG.info("Persisted " + entity.toString());
		}
	}

	protected abstract void persistEntity(T entity);
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}

}
