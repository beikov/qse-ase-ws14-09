package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;

import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class UserDataAccessTest extends AbstractContainerTest<UserDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private UserDataAccess userDataAccess;
	
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
