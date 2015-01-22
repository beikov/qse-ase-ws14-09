package at.ac.tuwien.ase09.service;

import java.util.Calendar;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.PortfolioFollowerAddedNotification;

@Stateless
public class PortfolioService extends AbstractService {
	
	@Inject
	private NotificationService notificationService;
	
	public Portfolio createPortfolio(Portfolio portfolio) {
		portfolio.setOwner(em.getReference(User.class, userContext.getUserId()));
		em.persist(portfolio);
		return portfolio;
	}
	
	public Portfolio updatePortfolio(Portfolio portfolio){
		return em.merge(portfolio);
	}

	public void removePortfolio(Portfolio portfolio) {
		portfolio.setDeleted(true);
		updatePortfolio(portfolio);
		//em.remove(em.contains(portfolio) ? portfolio : em.merge(portfolio));
	}
	
	public Portfolio followPortfolio(Portfolio portfolioToFollow, Long followerId) {
    	Set<User> followers = portfolioToFollow.getFollowers();
    	followers.add(em.getReference(User.class, followerId));
    	portfolioToFollow.setFollowers(followers);
    	PortfolioFollowerAddedNotification fan = new PortfolioFollowerAddedNotification();
    	fan.setCreated(Calendar.getInstance());
    	fan.setFollower(em.getReference(User.class, followerId));
    	fan.setUser(portfolioToFollow.getOwner());
    	fan.setPortfolio(portfolioToFollow);
    	notificationService.addNotification(fan);
    	// TODO: fix notification creation
    	/*FollowerAddedNotification fan = new FollowerAddedNotification();
    	fan.setCreated(Calendar.getInstance());
    	fan.setFollower(follower);
    	fan.setUser(portfolioToFollow);
    	notificationService.addNotification(fan);*/
    	return updatePortfolio(portfolioToFollow);
    }
    
    public Portfolio unfollowPortfolio(Portfolio portfolioToUnfollow, Long followerId) {
    	portfolioToUnfollow.getFollowers().remove(em.getReference(User.class, followerId));
    	//savePortfolio(portfolioToUnfollow);
    	return updatePortfolio(portfolioToUnfollow);
    }
	
}
