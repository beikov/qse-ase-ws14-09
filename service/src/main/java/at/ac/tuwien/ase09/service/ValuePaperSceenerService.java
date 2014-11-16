package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.ValuePaperScreenerAccess;
import at.ac.tuwien.ase09.model.ValuePaper;

@Stateless
public class ValuePaperSceenerService {

	@Inject
	private ValuePaperScreenerAccess valuePaperScreenerAccess;
	
	public List<ValuePaper> search(ValuePaper valuePaper,Boolean isTypeSpecificated) {
		return valuePaperScreenerAccess.findByValuePaper(valuePaper,isTypeSpecificated);
	}
}
