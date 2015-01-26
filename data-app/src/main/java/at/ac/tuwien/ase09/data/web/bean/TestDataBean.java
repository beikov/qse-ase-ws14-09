package at.ac.tuwien.ase09.data.web.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.StockDataAccess;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.service.ValuePaperPriceEntryService;

@Named
@RequestScoped
public class TestDataBean {

	@Inject
	private StockDataAccess stockDataAccess;
	@Inject
	private ValuePaperPriceEntryService valuePaperPriceEntryService;
	
	private String code;
	private BigDecimal price;
	
	@Named("testDataStockItems")
	@RequestScoped
	@Produces
	public List<SelectItem> getStockItems() {
		List<Stock> stocks = stockDataAccess.getStocks();
		List<SelectItem> items = new ArrayList<SelectItem>(stocks.size());
		
		for (Stock s : stocks) {
			items.add(new SelectItem(s.getCode(), s.getName()));
		}
		
		return items;
	}
	
	public void create() {
		valuePaperPriceEntryService.savePriceEntry(code, price);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
