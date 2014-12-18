package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.cep.EventProcessingSingleton;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;
import at.ac.tuwien.ase09.model.Stock;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.event.ValuePaperPriceEntryDTO;

@Stateless
public class StockService {
	@Inject
	private EntityManager em;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;

	@Inject
	@Added
	private Event<ValuePaperPriceEntry> priceEntryAdded;
	@Inject
	private EventProcessingSingleton epService;

	public void savePriceEntry(ValuePaperPriceEntry pe) {
		em.persist(pe);
		priceEntryAdded.fire(pe);
	}

	public void savePriceEntry(String code, BigDecimal price) {
		ValuePaperPriceEntry priceEntry = new ValuePaperPriceEntry();
		priceEntry.setPrice(price);
		priceEntry.setValuePaper(valuePaperDataAccess.getValuePaperByCode(code, ValuePaper.class));
		em.persist(priceEntry);
	}

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
	}

	public void saveStockDividends(Stock stock, List<DividendHistoryEntry> dividendHistoryEntries) {
		for (DividendHistoryEntry historyEntry : dividendHistoryEntries) {
			em.persist(historyEntry);
		}
	}

	public void onPriceEntryAdded(@Observes(during = TransactionPhase.AFTER_COMPLETION) @Added ValuePaperPriceEntry pe) {
		epService.addEvent(new ValuePaperPriceEntryDTO(pe));
	}

}
