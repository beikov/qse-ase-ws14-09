package at.ac.tuwien.ase09.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.service.NotificationService;
import at.ac.tuwien.ase09.service.UserService;
import at.ac.tuwien.ase09.test.AbstractServiceTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class NotificationServiceTest  extends AbstractServiceTest<NotificationServiceTest> {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private NotificationService service;
	
	@Inject
	private UserService userService;
	
	@Inject
	private NotificationDataAccess data;

	private FollowerAddedNotification fn;

	private User u1,u2;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return createServiceTestBaseDeployment()
				.addClasses(
						UserService.class,
						NotificationService.class,
						NotificationDataAccess.class);
	}
	
	@Before
	public void init(){
		u1 = new User();
		u1.setUsername("user1");
		userService.saveUser(u1);

		u2 = new User();
		u2.setUsername("user2");
		userService.saveUser(u2);
		
		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
	}

	
	@Test
	public void testAddNotificationAddsNotification(){
		service.addNotification(fn);
		assertEquals(fn, data.getNotificationsForUser(u1.getId()).get(0));
	}
	
	@Test
	public void testSetReadSetsNotificationRead(){
		service.addNotification(fn);
		Notification n = data.getNotificationsForUser(u1.getId()).get(0);
		assertFalse(n.getRead());
		service.setRead(n);
		assertTrue(data.getNotificationsForUser(u1.getId()).get(0).getRead());
	}
	
	@Test
	public void testSetPushedSetsNotificationPushed(){
		service.addNotification(fn);
		Notification n = data.getNotificationsForUser(u1.getId()).get(0);
		assertFalse(n.getPushed());
		service.setPushed(n);
		assertTrue(data.getNotificationsForUser(u1.getId()).get(0).getPushed());
	}
}
