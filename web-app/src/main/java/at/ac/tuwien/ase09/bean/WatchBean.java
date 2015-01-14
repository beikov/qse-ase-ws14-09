package at.ac.tuwien.ase09.bean;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.converter.SelectItemListConverter;
import at.ac.tuwien.ase09.data.StockDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.Stock;

@Named
@RequestScoped
public class WatchBean {

	private String watchExpression = "(MARKET_CAP < 9829 AND ENTERPRISE_VALUE > SQRT(REVENUE*3) + 100) OR TOTAL_CASH < 0";
	private Stock stock;
	
	@Inject
	private UserContext userContext;
	
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
		return new SelectItemListConverter(getStockItems());
	}
	
	// Getters + Setters

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public String getWatchExpression() {
		return watchExpression;
	}

	public void setWatchExpression(String watchExpression) {
		this.watchExpression = watchExpression;
	}
}
