package at.ac.tuwien.ase09.data.stock.atx.intraday;

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

import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Dependent
@Named("PriceListWriter")
public class PriceListWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger.getLogger(PriceListWriter.class.getName());
	@Inject
	private ValuePaperPriceEntryService service;

	@Inject
	private EntityManager em;
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		if(checkpoint != null){
			em.clear();
		}
	}
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		em.setFlushMode(FlushModeType.COMMIT);
		for(Object item : items){
			List<IntradayPrice> priceList = (List<IntradayPrice>) item;
			for(IntradayPrice price : priceList){
				service.savePriceEntry(price.getIsin(), price.getPrice());
			}
			em.flush();
			em.clear();
			LOG.info("Saved " + priceList.size());
		}
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}

}
