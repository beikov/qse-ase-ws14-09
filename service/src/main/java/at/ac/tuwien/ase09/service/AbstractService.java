package at.ac.tuwien.ase09.service;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class AbstractService {

	@Inject
	protected EntityManager em;
}
