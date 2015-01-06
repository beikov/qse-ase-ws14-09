package at.ac.tuwien.ase09.test.persistence;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.persistence.EntityManagerProducer;

public class TestEntityManagerProducer {
	@Produces
	@PersistenceContext(unitName = "PortfolioTestPU")
	private EntityManager em;
	
}
