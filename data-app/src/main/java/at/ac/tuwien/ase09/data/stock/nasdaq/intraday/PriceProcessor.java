package at.ac.tuwien.ase09.data.stock.nasdaq.intraday;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.AbstractPriceProcessor;
import at.ac.tuwien.ase09.data.model.IntradayPrice;

@Dependent
@Named
public class PriceProcessor extends AbstractPriceProcessor {
	
	@Override
	public Object processItem(Object item) throws Exception {
		IntradayPrice price = (IntradayPrice) item;
		if(isPriceSavingRequired(price)){
			return price;
		}else{
			return null;
		}
	}

}
