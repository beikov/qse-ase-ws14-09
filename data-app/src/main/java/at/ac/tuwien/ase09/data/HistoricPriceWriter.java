package at.ac.tuwien.ase09.data;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.service.ValuePaperService;

@Dependent
@Named
public class HistoricPriceWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(AbstractEntityWriter.class.getName());
	@Inject
	private ValuePaperService valuePaperService;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
//		if(checkpoint != null){
//			em.clear();
//		}
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for(int i = 0; i < items.size(); i++){
			List<ValuePaperHistoryEntry> historyEntryList = (List<ValuePaperHistoryEntry>) items.get(i);
			valuePaperService.saveHistory(historyEntryList);
			LOG.info("Saved " + historyEntryList.size() + " history entries");
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}
}
