package at.ac.tuwien.ase09.data;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

public abstract class AbstractPriceProcessor implements ItemProcessor {
	@Inject
	private ValuePaperPriceEntryDataAccess priceEntryDataAccess;
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	protected boolean isPriceSavingRequired(IntradayPrice price){
		if(price.getIsin() == null){
			return false;
		}
		try{
			ValuePaperPriceEntry existingPriceEntry = priceEntryDataAccess.getLastPriceEntry(price.getIsin());
			if(price.getPrice().compareTo(existingPriceEntry.getPrice()) == 0){
				// price has not changed so skip this item
				return false;
			}
		}catch(EntityNotFoundException e1){
			// check if the value paper itself exists in our db
			try{
				valuePaperDataAccess.getValuePaperByCode(price.getIsin(), Stock.class);
			}catch(EntityNotFoundException e2) {
				// if the value paper is unknown, skip this item
				return false;
			}
		}
		return true;
	}
}
