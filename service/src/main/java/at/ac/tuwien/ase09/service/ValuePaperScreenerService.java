package at.ac.tuwien.ase09.service;

import java.util.Currency;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.filter.AttributeFilter;
import at.ac.tuwien.ase09.model.ValuePaper;
import at.ac.tuwien.ase09.model.ValuePaperType;

@Stateless
public class ValuePaperScreenerService {

	@Inject
	private ValuePaperScreenerAccess valuePaperScreenerAccess;
	
	public List<ValuePaper> search(ValuePaper valuePaper,Boolean isTypeSpecificated) {
		return valuePaperScreenerAccess.findByValuePaper(valuePaper,isTypeSpecificated);
	}
	public List<Currency> getUsedCurrencyCodes()
	{
		return valuePaperScreenerAccess.getUsedCurrencyCodes();
	}
	public List<ValuePaper> search(List<AttributeFilter> filters,ValuePaperType type) 
	{
		return valuePaperScreenerAccess.findByFilter(filters, type);
	}
}
