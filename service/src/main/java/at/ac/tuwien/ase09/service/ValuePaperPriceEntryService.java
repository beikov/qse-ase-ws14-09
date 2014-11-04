package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.model.ValuePaperPriceEntry;

@Stateless
public class ValuePaperPriceEntryService {
	@PersistenceContext
	private EntityManager em;
	
	public void savePriceEntry(ValuePaperPriceEntry pe){
		em.persist(pe);
	}
}
