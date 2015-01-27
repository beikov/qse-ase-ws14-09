package at.ac.tuwien.ase09.service;

import java.math.BigDecimal;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.event.Added;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;
import at.ac.tuwien.ase09.model.event.ValuePaperPriceEntryDTO;

@Stateless
public class ValuePaperPriceEntryService extends AbstractService {

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	@Inject
	@Added
	private Event<ValuePaperPriceEntryDTO> priceEntryAdded;

	public void savePriceEntry(String code, BigDecimal price) {
		ValuePaperPriceEntry priceEntry = new ValuePaperPriceEntry();
		priceEntry.setPrice(price);
		priceEntry.setValuePaper(valuePaperDataAccess.getValuePaperByCode(code, ValuePaper.class));
		em.persist(priceEntry);
		priceEntryAdded.fire(new ValuePaperPriceEntryDTO(priceEntry));
	}
	
}
