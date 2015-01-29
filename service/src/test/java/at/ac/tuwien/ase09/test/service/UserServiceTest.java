package at.ac.tuwien.ase09.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.NotificationService;
import at.ac.tuwien.ase09.service.PortfolioService;
import at.ac.tuwien.ase09.service.UserService;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.Assert;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class UserServiceTest extends AbstractServiceTest<UserServiceTest>{
	private static final long serialVersionUID = 1L;
	
	@Inject
	private UserService userService;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(
						UserService.class,
						NotificationService.class,
						NotificationDataAccess.class);
	}
	
	@Test
	public void testSaveUser(){
		// Given
		User f1 = new User();
		f1.setUsername("F1");
		User f2 = new User();
		f2.setUsername("F2");
		dataManager.persist(f1);
		dataManager.persist(f2);
		em.clear();
		
		User user = new User();
		user.setUsername("Hansi");
		user.getFollowers().add(f1);
		user.getFollowers().add(f2);
		
		// When
		userService.saveUser(user);
		
		// Then
		User actual = em.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.followers f WHERE u.username = :username", User.class).setParameter("username", user.getUsername()).getSingleResult();
		assertEquals(user.getId(), actual.getId());
		assertEquals(user.getUsername(), actual.getUsername());
		Assert.assertUnorderedEquals(Arrays.asList(new User[]{f1, f2}), new ArrayList<>(actual.getFollowers()));
	}
	
}
