package at.ac.tuwien.ase09.data;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Dependent
@Named
public class IntradayPriceWriter extends AbstractEntityWriter<IntradayPrice> {
	@Inject
	private ValuePaperPriceEntryService service;

	@Override
	protected void persistEntity(IntradayPrice entity) {
		service.savePriceEntry(entity.getIsin(), entity.getPrice());
	}

}
