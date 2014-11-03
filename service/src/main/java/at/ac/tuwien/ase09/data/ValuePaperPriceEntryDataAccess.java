package at.ac.tuwien.ase09.data;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Stateless
public class ValuePaperPriceEntryDataAccess {

	@PersistenceContext
	private EntityManager em;
	
	public ValuePaperPriceEntry getLastPriceEntry(String isin){
		throw new UnsupportedOperationException();
	}
}
