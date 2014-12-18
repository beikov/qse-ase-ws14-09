package at.ac.tuwien.ase09.data.fund.intraday;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.model.IntradayPrice;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Dependent
@Named
public class FundPriceProcessor implements ItemProcessor {
	@Inject
	private ValuePaperPriceEntryDataAccess priceEntryDataAccess;
	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		IntradayPrice price = (IntradayPrice) item;
		try{
			ValuePaperPriceEntry existingPriceEntry = priceEntryDataAccess.getLastPriceEntry(price.getIsin());
			if(price.getPrice().compareTo(existingPriceEntry.getPrice()) == 0){
				// price has not changed so skip this item
				return null;
			}
		}catch(EntityNotFoundException e1){
			// check if the value paper itself exists in our db
			try{
				valuePaperDataAccess.getValuePaperByCode(price.getIsin(), Fund.class);
			}catch(EntityNotFoundException e2) {
				// if the value paper is unknown, skip this item
				return null;
			}
		}
		return price;
	}

}
