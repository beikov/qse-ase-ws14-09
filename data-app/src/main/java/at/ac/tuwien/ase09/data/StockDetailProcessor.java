package at.ac.tuwien.ase09.data;

import java.util.Iterator;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.data.model.StockDetailModel;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;

@Named
@Dependent
public class StockDetailProcessor implements ItemProcessor {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		StockDetailModel stockDetailModel = (StockDetailModel) item;
		DividendHistoryEntry latestHistoryEntry = null;
		try{
			latestHistoryEntry = valuePaperDataAccess.getLatestDividendHistoryEntry(stockDetailModel.getStock().getCode());
		}catch(EntityNotFoundException nre){
			// ignore
		}
		Iterator<DividendHistoryEntry> historyEntryIter = stockDetailModel.getDividendHistoryEntries().iterator();
		while(historyEntryIter.hasNext()){
			DividendHistoryEntry historyEntry = historyEntryIter.next();
			if(latestHistoryEntry != null && historyEntry.getDividendDate().compareTo(latestHistoryEntry.getDividendDate()) <= 0){
				historyEntryIter.remove();
			}
		}
		return stockDetailModel;
	}

}
