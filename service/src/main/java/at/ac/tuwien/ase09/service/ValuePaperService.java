package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;

@Stateless
public class ValuePaperService extends AbstractService {

	public void saveHistory(List<ValuePaperHistoryEntry> historyEntryList) {
		for(ValuePaperHistoryEntry historyEntry : historyEntryList){
			em.persist(historyEntry);
		}
		
		em.flush();
	}
}
