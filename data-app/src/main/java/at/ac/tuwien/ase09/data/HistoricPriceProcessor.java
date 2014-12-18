package at.ac.tuwien.ase09.data;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.ValuePaperHistoryEntry;

@Dependent
@Named
public class HistoricPriceProcessor implements ItemProcessor {
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceDataAccess;
	
	@Override
	public Object processItem(Object item) throws Exception {
		// Filter out prices that are already in the DB
		List<ValuePaperHistoryEntry> historicEntries = (List<ValuePaperHistoryEntry>) item;
		if(historicEntries.isEmpty()){
			return null;
		}
		String isin = historicEntries.get(0).getValuePaper().getCode();
		Set<Calendar> availableDates = new HashSet<>(valuePaperPriceDataAccess.getHistoricPriceEntryDates(isin));
		
		Iterator<ValuePaperHistoryEntry> iter = historicEntries.iterator();
		while(iter.hasNext()){
			if(availableDates.contains(iter.next().getDate())){
				iter.remove();
			}
		}
		if(historicEntries.isEmpty()){
			return null;
		}
		return historicEntries;
	}

}
