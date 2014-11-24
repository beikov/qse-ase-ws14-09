package at.ac.tuwien.ase09.data.stock.atx.intraday;

import java.util.Iterator;
import java.util.List;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractPriceProcessor;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Dependent
@Named("PriceListProcessor")
public class PriceListProcessor extends AbstractPriceProcessor {
	
	@Override
	public Object processItem(Object item) throws Exception {
		List<IntradayPrice> priceList = (List<IntradayPrice>) item;
		Iterator<IntradayPrice> priceIter = priceList.iterator();
		while(priceIter.hasNext()){
			IntradayPrice price = priceIter.next();
			if(!isPriceSavingRequired(price)){
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
