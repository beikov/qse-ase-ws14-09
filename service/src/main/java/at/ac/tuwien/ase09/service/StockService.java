package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.event.StockDTO;

@Stateless
public class StockService extends AbstractService {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;

	@Inject
	@Added
	private Event<StockDTO> stockUpdated;

	public void saveStock(Stock stock) {
		Stock existingStock = null;

		try {
			existingStock = valuePaperDataAccess.getValuePaperByCode(stock.getCode(), Stock.class);
			em.detach(existingStock);
		} catch (EntityNotFoundException nfe) {
			// ignore
		}

		if (existingStock != null) {
			stock.setId(existingStock.getId());
			em.merge(stock);
		} else {
			em.persist(stock);
		}
		
		stockUpdated.fire(new StockDTO(stock));
	}

	public void saveStockDividends(Stock stock, List<DividendHistoryEntry> dividendHistoryEntries) {
		for (DividendHistoryEntry historyEntry : dividendHistoryEntries) {
			em.persist(historyEntry);
		}
	}

}
