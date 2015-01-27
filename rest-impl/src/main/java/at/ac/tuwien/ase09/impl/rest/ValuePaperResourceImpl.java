package at.ac.tuwien.ase09.impl.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.model.Fund;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.StockBond;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.ValuePaperResource;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

@Stateless
public class ValuePaperResourceImpl extends AbstractResource implements ValuePaperResource{

	@Inject
	private ValuePaperScreenerAccess valuePaperScreenerDataAccess;
	
	@Inject
	private StockMarketGameDataAccess stockMarketGameDataAccess;
	
	@Override
	public List<ValuePaperDto> getValuePapers(long portfolioId, String filter,
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
		
		StockMarketGame stockMarketGame = stockMarketGameDataAccess.getStockMarketGameForPortfolio(portfolioId);
		
		List<ValuePaperDto> results = new ArrayList<>();
		List<ValuePaper> matchingValuePapers = valuePaperScreenerDataAccess.findByValuePaper(stockMarketGame == null ? null : stockMarketGame.getAllowedValuePapers(), valuePaperType, valuePaper);
		for(ValuePaper vp : matchingValuePapers){
			results.add(createFromEntity(vp));
		}
		
		return results;
	}

}
