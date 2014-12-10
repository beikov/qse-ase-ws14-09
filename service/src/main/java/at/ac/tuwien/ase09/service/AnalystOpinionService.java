package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.data.AnalystOpinionDataAccess;
import at.ac.tuwien.ase09.model.AnalystOpinion;

@Stateless
public class AnalystOpinionService {
	
	@PersistenceContext
	private EntityManager em;

	@Inject
	private AnalystOpinionDataAccess analystOpinionDataAccess;
	
	public List<AnalystOpinion> getAnalystOpinionsByValuePaperCode(String code){
		return analystOpinionDataAccess.getAnalystOpinionsByValuePaperCode(code);
	}
	
}
