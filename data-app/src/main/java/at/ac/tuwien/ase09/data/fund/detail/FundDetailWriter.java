package at.ac.tuwien.ase09.data.fund.detail;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractEntityWriter;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;

@Dependent
@Named
public class FundDetailWriter extends AbstractEntityWriter {
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	protected void persistEntity(Object entity) {
		Fund fund = (Fund) entity;
		Fund existingFund = null;
		try{
			existingFund = valuePaperDataAccess.getValuePaperByIsin(fund.getIsin(), Fund.class);
			// existingFund is managed
		}catch(EntityNotFoundException nfe){
			// ignore
		}
		if(existingFund != null){
			existingFund.setIsin(fund.getIsin());
			existingFund.setName(fund.getName());
			existingFund.setDetailUrl(fund.getDetailUrl());
		}else{
			em.persist(fund);
		}
	}

}
