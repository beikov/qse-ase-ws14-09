package at.ac.tuwien.ase09.impl.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.ValuePaperResource;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

@Stateless
public class ValuePaperResourceImpl implements ValuePaperResource{

	@Inject
	private ValuePaperScreenerAccess valuePaperScreenerDataAccess;
	
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;
	
	@Override
	public List<ValuePaperDto> getValuePapers(String filter,
			ValuePaperType valuePaperType) {
		ValuePaper  valuePaper = null;
		
		switch(valuePaperType){
			case STOCK:	Stock s = new Stock();
						s.setTickerSymbol(filter);
						valuePaper = s;
						break;
			case BOND:	valuePaper = new StockBond();
						break;
			case FUND:	
			default:	valuePaper = new Fund();
		}
		
		valuePaper.setName(filter);
		valuePaper.setCode(filter);
		
		List<ValuePaperDto> results = new ArrayList<>();
		List<ValuePaper> matchingValuePapers = valuePaperScreenerDataAccess.findByValuePaper(valuePaperType, valuePaper);
		for(ValuePaper vp : matchingValuePapers){
			Currency currency = null;
			if(vp instanceof Stock){
				currency = ((Stock) vp).getCurrency();
			}else if(vp instanceof Fund){
				currency = ((Fund) vp).getCurrency();
			}
			
			BigDecimal lastPrice = null;
			BigDecimal closingPrice = null;
			try{
				ValuePaperPriceEntry lastPriceEntry = valuePaperPriceEntryDataAccess.getLastPriceEntry(vp.getCode());
				lastPrice = lastPriceEntry.getPrice();
			}catch(EntityNotFoundException e){ /* ignore */ }
			
			try{
				Calendar yesterday = Calendar.getInstance();
				yesterday.roll(Calendar.DAY_OF_YEAR, -1);
				ValuePaperHistoryEntry historicPriceEntry = valuePaperPriceEntryDataAccess.getHistoricPriceEntry(vp.getCode(), yesterday);
				closingPrice = historicPriceEntry.getClosingPrice();
			}catch(EntityNotFoundException e) { /* ignore */ }
			results.add(new ValuePaperDto(vp.getName(), vp.getCode(), currency, lastPrice, closingPrice));
		}
		
		return results;
	}

}
