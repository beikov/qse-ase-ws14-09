package at.ac.tuwien.ase09.data.stock.atx.intraday;

import java.util.Iterator;
import java.util.List;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Dependent
@Named("PriceListProcessor")
public class PriceListProcessor implements ItemProcessor {
	@Inject
	private ValuePaperPriceEntryDataAccess priceEntryDataAccess;
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		List<IntradayPrice> priceList = (List<IntradayPrice>) item;
		Iterator<IntradayPrice> priceIter = priceList.iterator();
		while(priceIter.hasNext()){
			IntradayPrice price = priceIter.next();
			if(!savePriceRequired(price)){
				priceIter.remove();
			}
		}
		if(priceList.isEmpty()){
			return null;
		}else{
			return priceList;
		}
	}
	
	private boolean savePriceRequired(IntradayPrice price){
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
