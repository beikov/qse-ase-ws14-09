package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.DividendHistoryEntryDataAccess;
import at.ac.tuwien.ase09.model.DividendHistoryEntry;

@Stateless
public class DividendHistoryEntryService {
	
	@Inject
	private EntityManager em;

	@Inject
	private DividendHistoryEntryDataAccess dividendHistoryEntryDataAccess;
	
	public List<DividendHistoryEntry> getDividendHistoryEntryByValuePaperCode(String code){
		return dividendHistoryEntryDataAccess.getDividendHistoryEntryByValuePaperCode(code);
	}
}
