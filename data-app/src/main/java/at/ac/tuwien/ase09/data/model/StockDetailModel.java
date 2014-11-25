package at.ac.tuwien.ase09.data.model;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;

public class StockDetailModel {
	private final Stock stock;
	private final List<DividendHistoryEntry> dividendHistory = new ArrayList<DividendHistoryEntry>();
	
	public StockDetailModel(Stock stock) {
		super();
		this.stock = stock;
	}

	public Stock getStock() {
		return stock;
	}

	public List<DividendHistoryEntry> getDividendHistoryEntries() {
		return dividendHistory;
	}
	
}
