package at.ac.tuwien.ase09.impl.rest;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

public class AbstractResource {

	@Inject
	protected ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;
	
	protected ValuePaperDto createFromEntity(ValuePaper valuePaper){
		Currency currency = null;
		if(valuePaper instanceof Stock){
			currency = ((Stock) valuePaper).getCurrency();
		}else if(valuePaper instanceof Fund){
			currency = ((Fund) valuePaper).getCurrency();
		}
		
		BigDecimal lastPrice = null;
		BigDecimal closingPrice = null;
		try{
			ValuePaperPriceEntry lastPriceEntry = valuePaperPriceEntryDataAccess.getLastPriceEntry(valuePaper.getCode());
			lastPrice = lastPriceEntry.getPrice();
		}catch(EntityNotFoundException e){ /* ignore */ }
		
		try{
			Calendar yesterday = Calendar.getInstance();
			yesterday.roll(Calendar.DAY_OF_YEAR, -1);
			ValuePaperHistoryEntry historicPriceEntry = valuePaperPriceEntryDataAccess.getHistoricPriceEntry(valuePaper.getCode(), yesterday);
			closingPrice = historicPriceEntry.getClosingPrice();
		}catch(EntityNotFoundException e) { /* ignore */ }
		return new ValuePaperDto(valuePaper.getId(), valuePaper.getName(), valuePaper.getCode(), currency, lastPrice, closingPrice);
	}
	
	protected PortfolioValuePaperDto createFromEntity(PortfolioValuePaper portfolioValuePaper){
		ValuePaperDto valuePaperDto = createFromEntity(portfolioValuePaper.getValuePaper());
		return new PortfolioValuePaperDto(portfolioValuePaper.getId(), valuePaperDto, portfolioValuePaper.getBuyPrice(), portfolioValuePaper.getVolume());
	}
}
