package at.ac.tuwien.ase09.data.stock.atx.intraday;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Dependent
@Named("PriceListWriter")
public class PriceListWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(PriceListWriter.class.getName());
	@Inject
	private ValuePaperPriceEntryService service;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		// if(checkpoint != null){
		// em.clear();
		// }
	}

	@Override
	public void writeItems(List<Object> items) throws Exception {
		for (Object item : items) {
			List<IntradayPrice> priceList = (List<IntradayPrice>) item;
			for (IntradayPrice price : priceList) {
				service.savePriceEntry(price.getIsin(), price.getPrice());
			}
			LOG.info("Saved " + priceList.size() + " intraday prices");
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}

}
