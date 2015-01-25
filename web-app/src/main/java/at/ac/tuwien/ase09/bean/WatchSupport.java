package at.ac.tuwien.ase09.bean;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.converter.SelectItemListConverter;
import at.ac.tuwien.ase09.data.StockDataAccess;
import at.ac.tuwien.ase09.model.Stock;

public class WatchSupport {
	
	@Inject
	private StockDataAccess stockDataAccess;
	
	@Named("stockItems")
	@RequestScoped
	public List<SelectItem> getStockItems() {
		List<Stock> stocks = stockDataAccess.getAllowedStocks();
		List<SelectItem> stockItems = new ArrayList<SelectItem>(stocks.size());
		
		for (Stock s : stocks) {
			stockItems.add(new SelectItem(s, s.getName()));
		}
		
		return stockItems;
	}
	
	@Named("stockConverter")
	@RequestScoped
	public Converter getStockConverter(@Named("stockItems") List<SelectItem> stockItems) {
		return new SelectItemListConverter(stockItems);
	}

}
