package at.ac.tuwien.ase09.data;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.context.UserContext;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class AbstractDataAccess {

	@Inject
	protected EntityManager em;
	
	@Inject
	protected UserContext userContext;
}
