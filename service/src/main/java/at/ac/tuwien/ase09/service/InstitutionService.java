package at.ac.tuwien.ase09.service;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Institution;

@Stateless
public class InstitutionService {
	
	@Inject
	private EntityManager em;

	public void update(Institution institution) {
		em.merge(institution);
	}

}
