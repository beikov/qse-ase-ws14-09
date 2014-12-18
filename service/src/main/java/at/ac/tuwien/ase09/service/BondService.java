package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.StockBond;

@Stateless
public class BondService extends AbstractService {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;

	public void saveBond(StockBond bond) {
		StockBond existingBond = null;

		try {
			existingBond = valuePaperDataAccess.getValuePaperByCode(bond.getCode(), StockBond.class);
		} catch (EntityNotFoundException nfe) {
			// ignore
		}

		if (existingBond != null) {
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
		} else {
			em.persist(bond);
		}
	}
}
