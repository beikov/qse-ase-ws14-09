package at.ac.tuwien.ase09.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {

	@Produces
	@PersistenceContext(unitName = "PortfolioPU")
	EntityManager em;
}
