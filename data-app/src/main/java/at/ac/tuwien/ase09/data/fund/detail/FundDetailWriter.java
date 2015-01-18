package at.ac.tuwien.ase09.data.fund.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.service.FundService;

@Dependent
@Named
public class FundDetailWriter extends AbstractEntityWriter<Fund> {
	
	@Inject
	private FundService fundService;

	@Override
	protected void persistEntity(Fund entity) {
		fundService.saveFund(entity);
	}

}
