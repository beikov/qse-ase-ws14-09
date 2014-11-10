package at.ac.tuwien.ase09.data.bond.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Bond;

@Dependent
@Named("BondDetailWriter")
public class BondDetailWriter extends AbstractEntityWriter {
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	protected void persistEntity(Object entity) {
		Bond bond = (Bond) entity;
		Bond existingBond = null;
		try{
			existingBond = valuePaperDataAccess.getBondByIsin(bond.getIsin());
		}catch(EntityNotFoundException nfe){
			// ignore
		}
		// TODO: set id on entity or copy fields to attached existingEntity?
		if(existingBond != null){
			existingBond.setCurrency(bond.getCurrency());
			existingBond.setIsin(bond.getIsin());
			existingBond.setName(bond.getName());
		}else{
			em.persist(entity);
		}
	}

}
