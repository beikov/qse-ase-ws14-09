package at.ac.tuwien.ase09.data;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.model.AnalystOpinion;

@Stateless
public class AnalystOpinionDataAccess {
	@Inject
	private EntityManager em;

	public List<AnalystOpinion> getAnalystOpinionsByValuePaperCode(String code){
		
		//TBD
		/*
		try{
			return em.createQuery("SELECT a FROM AnalystOpinion n WHERE n.valuePaper.code = :code", AnalystOpinion.class).setParameter("code", code).getResultList();
		}catch(Exception e){
			throw new AppException(e);
		}*/
		
		return null;
	}
}
