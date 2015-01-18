package at.ac.tuwien.ase09.data.stock.analystOpinion;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.model.AnalystOpinion;
import at.ac.tuwien.ase09.service.AnalystOpinionService;

@Dependent
@Named
public class AnalystOpinionWriter extends AbstractItemWriter {
	private static final Logger LOG = Logger
			.getLogger(AnalystOpinionWriter.class.getName());

	@Inject
	private AnalystOpinionService service;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		// if(checkpoint != null){
		// em.clear();
		// }
	}

	@Override
	public void writeItems(List<Object> items) throws Exception {
		for (Object item : items) {
			List<AnalystOpinion> analystOpinions = (List<AnalystOpinion>) item;
			service.saveOpionions(analystOpinions);
			LOG.info("Saved " + analystOpinions.size() + " analyst opinions");
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return 0;
	}
}
