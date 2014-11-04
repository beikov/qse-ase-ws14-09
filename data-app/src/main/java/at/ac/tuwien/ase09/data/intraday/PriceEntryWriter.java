package at.ac.tuwien.ase09.data.intraday;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Dependent
@Named("PriceEntryWriter")
public class PriceEntryWriter extends AbstractItemWriter {

	@Inject
	private ValuePaperPriceEntryService service;
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for(Object item : items){
			service.savePriceEntry((ValuePaperPriceEntry) item);
		}
		
	}

}
