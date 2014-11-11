package at.ac.tuwien.ase09.data.stock.detail;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;

@Dependent
@Named("HistoricPriceWriter")
public class HistoricPriceWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(AbstractEntityWriter.class.getName());
	@PersistenceContext
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
		int numWrittenItems = 0;
		for(int i = 0; i < items.size(); i++){
			List<ValuePaperHistoryEntry> priceEntryList = (List<ValuePaperHistoryEntry>) items.get(i);
			for(ValuePaperHistoryEntry priceEntry : priceEntryList){
				em.persist(priceEntry);
				numWrittenItems++;
				LOG.info("Persisted " + priceEntry.toString());
			}
			if(numWrittenItems % 20 == 0){
				em.flush();
				em.clear();
			}
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}
}
