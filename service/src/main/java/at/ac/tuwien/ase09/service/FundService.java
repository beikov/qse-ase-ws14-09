package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;

@Stateless
public class FundService extends AbstractService {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;

	public void saveFund(Fund fund) {
		Fund existingFund = null;
		
		try {
			existingFund = valuePaperDataAccess.getValuePaperByCode(fund.getCode(), Fund.class);
			// existingFund is managed
		} catch (EntityNotFoundException nfe) {
			// ignore
		}
		
		if (existingFund != null) {
			existingFund.setCode(fund.getCode());
			existingFund.setName(fund.getName());
			existingFund.setDetailUrl(fund.getDetailUrl());
			existingFund.setHistoricPricesPageUrl(fund.getHistoricPricesPageUrl());
			existingFund.setBusinessYearStartDay(fund.getBusinessYearStartDay());
			existingFund.setBusinessYearStartMonth(fund.getBusinessYearStartMonth());
			existingFund.setCategory(fund.getCategory());
			existingFund.setDenomination(fund.getDenomination());
			existingFund.setDepotBank(fund.getDepotBank());
			existingFund.setYieldType(fund.getYieldType());
		} else {
			em.persist(fund);
		}
	}
}
