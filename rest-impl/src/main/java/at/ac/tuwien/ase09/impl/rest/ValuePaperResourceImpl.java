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
	public List<ValuePaperDto> getPortfolios(String filter,
			ValuePaperType valuePaperType) {
		ValuePaper  valuePaper = null;
		
		switch(valuePaperType){
			case STOCK:	Stock s = new Stock();
						s.setTickerSymbol(filter);
						valuePaper = s;
						break;
			case BOND:	valuePaper = new StockBond();
						break;
			case FUND:	valuePaper = new Fund();
						break;
		}
		
		valuePaper.setName(filter);
		valuePaper.setCode(filter);
		
		List<ValuePaperDto> results = new ArrayList<>();
		List<ValuePaper> matchingValuePapers = valuePaperScreenerDataAccess.findByValuePaper(valuePaper);
		for(ValuePaper vp : matchingValuePapers){
			Currency currency = null;
			if(vp instanceof Stock){
				currency = ((Stock) vp).getCurrency();
			}else if(vp instanceof Fund){
				currency = ((Fund) vp).getCurrency();
			}
			
			ValuePaperPriceEntry lastPriceEntry = valuePaperPriceEntryDataAccess.getLastPriceEntry(vp.getCode());
			BigDecimal dayHighPrice = valuePaperPriceEntryDataAccess.getDayHighPrice(vp.getCode());
			BigDecimal dayLowPrice = valuePaperPriceEntryDataAccess.getDayLowPrice(vp.getCode());
			Calendar yesterday = Calendar.getInstance();
			yesterday.roll(Calendar.DAY_OF_YEAR, -1);
			ValuePaperHistoryEntry historicPriceEntry = valuePaperPriceEntryDataAccess.getHistoricPriceEntry(vp.getCode(), yesterday);
			
			results.add(new ValuePaperDto(vp.getName(), vp.getCode(), currency, lastPriceEntry.getPrice(), historicPriceEntry.getClosingPrice(), dayHighPrice, dayLowPrice));
		}
		
		return results;
	}

}
