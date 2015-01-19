package at.ac.tuwien.ase09.test.data;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.ase09.data.NotificationDataAccess;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.model.notification.Notification;
import at.ac.tuwien.ase09.service.NotificationService;
import at.ac.tuwien.ase09.service.UserService;
import at.ac.tuwien.ase09.test.AbstractContainerTest;
import at.ac.tuwien.ase09.test.DatabaseAware;

@DatabaseAware
public class NotificationDataAccessTest extends AbstractContainerTest<NotificationDataAccessTest>{
	private static final long serialVersionUID = 1L;

	@Inject
	private NotificationDataAccess data;
	
	@Inject
	private UserService userService;

	@Inject
	private NotificationService notiService;
	
	private User u1,u2;

	@Before
	public void init(){
		u1 = new User();
		u1.setUsername("user1");
		userService.saveUser(u1);

		u2 = new User();
		u2.setUsername("user2");
		userService.saveUser(u2);
	}

	@Test
	public void testGetNotificationsForUserReturnsNotifications(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		notiService.addNotification(fn);

		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		notiService.addNotification(fn);

		assertTrue(data.getNotificationsForUser(u1).size() == 2);
	}

	@Test
	public void testgetNotificationsForUserReturnsOnlyNotificationsForThisUser(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		notiService.addNotification(fn);

		//second notification belongs to user u2 not u1
		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u1);
		fn.setUser(u2);
		notiService.addNotification(fn);

		assertTrue(data.getNotificationsForUser(u1).size() == 1);
	}


	@Test
	public void testgetNotificationsForUserNotificationsInRightOrder(){
		List<FollowerAddedNotification> notifications = new ArrayList<>();
		FollowerAddedNotification fn;

		for(int i=0; i<3; i++){
			fn = new FollowerAddedNotification();
			Calendar created = Calendar.getInstance();
			created.setTime(new Date(i));
			fn.setCreated(created);
			fn.setFollower(u2);
			fn.setUser(u1);
			notiService.addNotification(fn);
			notifications.add(fn);
		}
		
		List<? extends Notification> received = data.getNotificationsForUser(u1);
		for(int i=2; i>=0; i--){
			assertEquals(received.get(i),notifications.get(i));
		}
	}

	@Test
	public void testgetUnreadNotificationsForUserReturnsNotifications(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(false);
		notiService.addNotification(fn);
		
		assertNotNull(data.getUnreadNotificationsForUser(u1));
	}

	@Test
	public void testgetUnreadNotificationsForUserReturnsOnlyUnreadNotifications(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(false);
		notiService.addNotification(fn);
		
		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(true);
		notiService.addNotification(fn);
		
		assertEquals(data.getUnreadNotificationsForUser(u1).size(), 1);
	}
	
	
	@Test
	public void testGetUnreadnotificationsCountReturnsOne(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(false);
		notiService.addNotification(fn);
		
		assertEquals(1, data.getUnreadNotificationsCount(u1));
	}
	
	
	@Test
	public void testGetUnreadnotificationsCountReturnsZero(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(true);
		notiService.addNotification(fn);
		
		assertEquals(0, data.getUnreadNotificationsCount(u1));
	}
	
	@Test
	public void testGetUnreadnotificationsCountReturnsPositiveCount(){
		FollowerAddedNotification fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(false);
		notiService.addNotification(fn);
		
		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(false);
		notiService.addNotification(fn);
		
		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(true);
		notiService.addNotification(fn);
		
		fn = new FollowerAddedNotification();
		fn.setCreated(Calendar.getInstance());
		fn.setFollower(u2);
		fn.setUser(u1);
		fn.setRead(false);
		notiService.addNotification(fn);
		
		assertEquals(3, data.getUnreadNotificationsCount(u1));
	}
	
	



}
