package at.ac.tuwien.ase09.test.persistence;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import at.ac.tuwien.ase09.persistence.EntityManagerProducer;

@Alternative
public class TestEntityManagerProducer extends EntityManagerProducer {
	@Produces
	@PersistenceContext(unitName = "PortfolioTestPU")
	private EntityManager em;
	
}
