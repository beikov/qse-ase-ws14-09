package at.ac.tuwien.ase09.service;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.model.AnalystOpinion;

@Stateless
public class AnalystOpinionService extends AbstractService {

	public void saveOpionions(List<AnalystOpinion> analystOpinions) {
		for (AnalystOpinion analystOpinion : analystOpinions) {
			em.persist(analystOpinion);
		}
		
		em.flush();
	}
}
