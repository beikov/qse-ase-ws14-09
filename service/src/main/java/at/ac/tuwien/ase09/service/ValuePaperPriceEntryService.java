package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Stateless
public class ValuePaperPriceEntryService {
	@Inject
	private EntityManager em;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;

	public void savePriceEntry(ValuePaperPriceEntry pe) {
		em.persist(pe);
	}

	public void savePriceEntry(String code, BigDecimal price) {
		ValuePaperPriceEntry priceEntry = new ValuePaperPriceEntry();
		priceEntry.setPrice(price);
		priceEntry.setValuePaper(valuePaperDataAccess.getValuePaperByCode(code, ValuePaper.class));
		savePriceEntry(priceEntry);
	}
	
	public ValuePaperPriceEntry getLastPriceEntry(String code){
		return valuePaperPriceEntryDataAccess.getLastPriceEntry(code);
	}
	
	public List<ValuePaperHistoryEntry> getValuePaperPriceHistoryEntries(String code){
		return valuePaperPriceEntryDataAccess.getValuePaperPriceHistoryEntries(code);
	}
}
