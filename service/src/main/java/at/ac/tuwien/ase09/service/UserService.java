package at.ac.tuwien.ase09.service;

import java.util.Calendar;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
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
    
    public void deleteLogo(User user) {
    	try {
    		em.createQuery("update User u set u.logo = null where u.username = :username").setParameter("username", user.getUsername()).executeUpdate();
    	} catch (NoResultException e) {
			throw new EntityNotFoundException(e);
		} catch (Exception e) {
			throw new AppException(e);
		}
    }

}
