package at.ac.tuwien.ase09.service;

import java.util.Calendar;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;

@Stateless
public class UserService {
	@Inject
	private EntityManager em;
	
	@Inject
	private NotificationService notificationService;
	
	public void saveUser(User user){
		em.persist(user);
	}
	
    public User updateUser(User user) {
        return em.merge(user);
    }
    
    public User followUser(User userToFollow, User follower){
    	Set<User> followers = userToFollow.getFollowers();
    	followers.add(follower);
    	userToFollow.setFollowers(followers);
    	FollowerAddedNotification fan = new FollowerAddedNotification();
    	fan.setCreated(Calendar.getInstance());
    	fan.setFollower(follower);
    	fan.setUser(userToFollow);
    	notificationService.addNotification(fan);
    	return updateUser(userToFollow);
    }
    
    public User unfollowUser(User userToUnfollow, User follower){
    	userToUnfollow.getFollowers().remove(follower);
    	return updateUser(userToUnfollow);
    }

}
