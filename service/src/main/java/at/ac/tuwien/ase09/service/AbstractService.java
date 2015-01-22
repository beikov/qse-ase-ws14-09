package at.ac.tuwien.ase09.service;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.context.UserContext;

public class AbstractService {

	@Inject
	protected EntityManager em;

	@Inject
	protected UserContext userContext;
}
