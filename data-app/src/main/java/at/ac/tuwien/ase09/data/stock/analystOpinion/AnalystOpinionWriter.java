package at.ac.tuwien.ase09.data.stock.analystOpinion;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.data.stock.atx.intraday.PriceListWriter;
import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.model.NewsItem;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Dependent
@Named
public class AnalystOpinionWriter extends AbstractItemWriter {
	
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
			List<AnalystOpinion> analystOpinions = (List<AnalystOpinion>) item;
			for(AnalystOpinion analystOpinion : analystOpinions){
				em.persist(analystOpinion);
			}
			em.flush();
			em.clear();
			LOG.info("Saved " + analystOpinions.size() + " analyst opinions");
		}
	}
	
	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}
}
