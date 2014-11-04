package at.ac.tuwien.ase09.data.intraday;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Dependent
@Named("PriceEntryProcessor")
public class PriceEntryProcessor implements ItemProcessor {
//	@Inject
	private ValuePaperPriceEntryDataAccess priceEntryDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		IntradayPrice price = (IntradayPrice) item;
		ValuePaperPriceEntry priceEntry = null;
		try{
			priceEntry = priceEntryDataAccess.getLastPriceEntry(price.getIsin());
		}catch(EntityNotFoundException nfe){
			// if the value paper is unknown, skip this item
			return null;
		}
		if(priceEntry.getPrice().equals(price.getPrice())){
			// price has not changed so skip this item
			return null;
		}
		return item;
	}

}
