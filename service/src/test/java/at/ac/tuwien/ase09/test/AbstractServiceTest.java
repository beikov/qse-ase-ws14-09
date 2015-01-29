/**
 * 
 */
package at.ac.tuwien.ase09.test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.test.persistence.DataManager;

@RunWith(ServerSideDatabaseAwareArquillianRunner.class)
public abstract class AbstractServiceTest<T extends AbstractServiceTest<T>> extends AbstractContainerTest<T> {

    private static final long serialVersionUID = -7248288932170947951L;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected EntityManager em;
    
    protected static WebArchive createServiceTestBaseDeployment(){
    	// necessary to add these classes?
    	return createContainerTestBaseDeployment()
    			.addPackage("at.ac.tuwien.ase09.test")
    			.addClass(AbstractContainerTest.class)
    			.addClass(UserContext.class)
    			.addClass(UserAccount.class)
    			.addClass(AbstractServiceTest.class);
    }
}
