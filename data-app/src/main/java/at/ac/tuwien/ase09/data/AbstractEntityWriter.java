package at.ac.tuwien.ase09.data;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;

public abstract class AbstractEntityWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(AbstractEntityWriter.class.getName());
	@Inject
	protected EntityManager em;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		if(checkpoint != null){
			em.clear();
		}
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		em.setFlushMode(FlushModeType.COMMIT);
		for(int i = 0; i < items.size(); i++){
			Object entity = items.get(i);
			persistEntity(entity);
			LOG.info("Persisted " + entity.toString());
		}
		em.flush();
		em.clear();
	}

	protected abstract void persistEntity(Object entity);
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}

}
