package at.ac.tuwien.ase09.service;

import java.util.Calendar;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.notification.FollowerAddedNotification;

@Stateless
public class PortfolioService {
	@Inject
	private EntityManager em;
	
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
