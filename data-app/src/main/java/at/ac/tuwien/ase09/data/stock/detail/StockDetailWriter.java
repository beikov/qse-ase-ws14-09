package at.ac.tuwien.ase09.data.stock.detail;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named("StockDetailWriter")
public class StockDetailWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(StockDetailWriter.class.getName());
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		if(checkpoint != null){
			em.clear();
		}
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for(int i = 0; i < items.size(); i++){
			Stock stock = (Stock) items.get(i);
			em.persist(stock);
			LOG.info("Persisted " + stock.toString());
		}
		em.flush();
		em.clear();
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}
}
