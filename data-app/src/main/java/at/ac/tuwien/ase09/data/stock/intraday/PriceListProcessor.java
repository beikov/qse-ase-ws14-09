package at.ac.tuwien.ase09.data.stock.intraday;

import java.util.Iterator;
import java.util.List;

import javax.batch.api.chunk.ItemProcessor;
import javax.ejb.EJBException;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Dependent
@Named("PriceListProcessor")
public class PriceListProcessor implements ItemProcessor {
	@Inject
	private ValuePaperPriceEntryDataAccess priceEntryDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		List<IntradayPrice> priceList = (List<IntradayPrice>) item;
		Iterator<IntradayPrice> priceIter = priceList.iterator();
		while(priceIter.hasNext()){
			IntradayPrice price = priceIter.next();
			ValuePaperPriceEntry priceEntry = null;
			try{
				priceEntry = priceEntryDataAccess.getLastPriceEntry(price.getIsin());
				if(priceEntry.getPrice().equals(price.getPrice())){
					// price has not changed so skip this item
					priceIter.remove();
				}
			}catch(EntityNotFoundException e){
				// if the value paper is unknown, skip this item
				priceIter.remove();
			}
			
		}
		if(priceList.isEmpty()){
			return null;
		}else{
			return priceList;
		}
	}

}
