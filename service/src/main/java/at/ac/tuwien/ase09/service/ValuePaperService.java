package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.ValuePaper;

@Stateless
public class ValuePaperService {
	
	@PersistenceContext
	private EntityManager em;

	@Inject
	private ValuePaperDataAccess valuePaperDataAccess;
	
	public ValuePaper getValuePaperByIsin(String isin){
		return valuePaperDataAccess.getValuePaperByIsin(isin);
	}
	
}
