package at.ac.tuwien.ase09.data.bond.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.service.BondService;

@Dependent
@Named
public class BondDetailWriter extends AbstractEntityWriter<StockBond> {
	@Inject
	private BondService bondService;
	
	@Override
	protected void persistEntity(StockBond entity) {
		bondService.saveBond(entity);
	}

}
