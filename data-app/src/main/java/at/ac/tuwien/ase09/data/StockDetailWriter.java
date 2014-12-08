package at.ac.tuwien.ase09.data;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.model.StockDetailModel;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;

@Dependent
@Named
public class StockDetailWriter extends AbstractEntityWriter {
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	protected void persistEntity(Object entity) {
		StockDetailModel stockDetailModel = (StockDetailModel) entity;
		Stock stock = stockDetailModel.getStock();
		Stock existingStock = null;
		try{
			existingStock = valuePaperDataAccess.getValuePaperByCode(stock.getCode(), Stock.class);
			// existingStock is managed
		}catch(EntityNotFoundException nfe){
			// ignore
		}
		
		// TODO: set id on entity or copy fields to attached existingEntity?
		if(existingStock != null){
			stock.setId(existingStock.getId());
			em.merge(stock);
		}else{
			em.persist(stock);
		}
		
		for(DividendHistoryEntry historyEntry : stockDetailModel.getDividendHistoryEntries()){
			em.persist(historyEntry);
		}
	}

}
