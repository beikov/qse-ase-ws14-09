package at.ac.tuwien.ase09.data.bond.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.StockBond;

@Dependent
@Named
public class BondDetailWriter extends AbstractEntityWriter {
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	protected void persistEntity(Object entity) {
		StockBond bond = (StockBond) entity;
		StockBond existingBond = null;
		try{
			existingBond = valuePaperDataAccess.getValuePaperByCode(bond.getCode(), StockBond.class);
		}catch(EntityNotFoundException nfe){
			// ignore
		}
		// TODO: set id on entity or copy fields to attached existingEntity?
		if(existingBond != null){
			existingBond.setCode(bond.getCode());
			existingBond.setName(bond.getName());
			existingBond.setBaseStock(bond.getBaseStock());
			existingBond.setHistoricPricesPageUrl(bond.getHistoricPricesPageUrl());
			existingBond.setDetailUrl(bond.getDetailUrl());
			existingBond.setEmissionDate(bond.getEmissionDate());
			existingBond.setEmissionPrice(bond.getEmissionPrice());
			existingBond.setCoupon(bond.getCoupon());
			existingBond.setEmitter(bond.getEmitter());
			existingBond.setEndDate(bond.getEndDate());
		}else{
			em.persist(entity);
		}
	}

}
