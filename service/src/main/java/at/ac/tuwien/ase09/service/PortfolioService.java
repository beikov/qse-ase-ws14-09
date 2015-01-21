package at.ac.tuwien.ase09.service;

import java.util.Calendar;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;
import at.ac.tuwien.ase09.model.notification.PortfolioFollowerAddedNotification;

@Stateless
public class PortfolioService {
	@Inject
	private EntityManager em;
	
	@Inject
	private NotificationService notificationService;
	
	public Portfolio savePortfolio(Portfolio portfolio){
		if(portfolio.getId() != null){
			if(em.find(Portfolio.class, portfolio.getId()) != null){
				return em.merge(portfolio);
			}
		}
		em.persist(portfolio);
		return portfolio;
	}

	public void removePortfolio(Portfolio portfolio) {
		em.remove(em.contains(portfolio) ? portfolio : em.merge(portfolio));
	}
	
	public Portfolio followPortfolio(Portfolio portfolioToFollow, User follower) {
    	Set<User> followers = portfolioToFollow.getFollowers();
    	followers.add(follower);
    	portfolioToFollow.setFollowers(followers);
    	PortfolioFollowerAddedNotification fan = new PortfolioFollowerAddedNotification();
    	fan.setCreated(Calendar.getInstance());
    	fan.setFollower(follower);
    	fan.setUser(portfolioToFollow.getOwner());
    	fan.setPortfolio(portfolioToFollow);
    	notificationService.addNotification(fan);
    	// TODO: fix notification creation
    	/*FollowerAddedNotification fan = new FollowerAddedNotification();
    	fan.setCreated(Calendar.getInstance());
    	fan.setFollower(follower);
    	fan.setUser(portfolioToFollow);
    	notificationService.addNotification(fan);*/
    	return savePortfolio(portfolioToFollow);
    }
    
    public Portfolio unfollowPortfolio(Portfolio portfolioToUnfollow, User follower) {
    	portfolioToUnfollow.getFollowers().remove(follower);
    	//savePortfolio(portfolioToUnfollow);
    	return savePortfolio(portfolioToUnfollow);
    }
	
}
