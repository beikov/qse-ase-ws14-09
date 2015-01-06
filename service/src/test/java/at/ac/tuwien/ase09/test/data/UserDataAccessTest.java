package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class UserDataAccessTest extends AbstractServiceTest<UserDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private UserDataAccess userDataAccess;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClass(UserDataAccess.class);
	}
	
	@Test
	public void testGetUserByUsername(){
		// Given
		final String username = "Hansi";
		User user = new User();
		user.setUsername(username);
		dataManager.persist(user);
		em.clear();
		
		// When
		User actual = userDataAccess.getUserByUsername(username);
		
		// Then
		assertEquals(user, actual);
	}
	
	@Test
	public void testGetUserByUsername_nonExistent(){
		Assert.verifyException(userDataAccess, EntityNotFoundException.class).getUserByUsername("ABC");
	}
}
