/**
 * 
 */
package at.ac.tuwien.ase09.test;

import java.io.Serializable;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.runner.RunWith;

@RunWith(ContainerRunner.class)
public abstract class AbstractContainerTest<T extends AbstractContainerTest<T>> implements Serializable {

    private static final long serialVersionUID = -7248288932170947951L;

    @Inject
    protected Instance<T> self;
    @Inject
    protected DefaultUserContext userContext;
    @Inject
    protected EntityManager em;
}
