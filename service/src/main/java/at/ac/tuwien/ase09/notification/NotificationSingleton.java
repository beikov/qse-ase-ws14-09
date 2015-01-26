package at.ac.tuwien.ase09.notification;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.PostRemove;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.notification.GameStartedNotification;
import at.ac.tuwien.ase09.service.NotificationService;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class NotificationSingleton {
	private Timer timer;
	
	private Map<Long, TimerTask> gameTasks;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private StockMarketGameDataAccess gameDataAccess;
	
	@Inject
	private NotificationService notificationService;
	
	@PostConstruct
	public void init(){
		gameTasks = new HashMap<Long, TimerTask>();
		timer = new Timer();
		List<StockMarketGame> unstartedGames = loadUnstartedGames();
		for (StockMarketGame stockMarketGame : unstartedGames) {
			addGame(stockMarketGame);
		}
	}
	
	@PostRemove
	public void cleanup(){
		timer.cancel();
	}

	public void addGame(StockMarketGame stockMarketGame) {
		if(gameTasks.containsKey(stockMarketGame.getId())){
			gameTasks.get(stockMarketGame).cancel();
			gameTasks.remove(stockMarketGame.getId());
		}
		TimerTask t = new NotifyStockMarketGamePlayersTask(stockMarketGame);
		gameTasks.put(stockMarketGame.getId(), t);
		timer.schedule(t, stockMarketGame.getValidFrom().getTime());
	}

	private List<StockMarketGame> loadUnstartedGames() {
		return gameDataAccess.getUnstartedStockMarketGames();
	}
	
	private class NotifyStockMarketGamePlayersTask extends TimerTask{
		
		private StockMarketGame game;
		
		public NotifyStockMarketGamePlayersTask(StockMarketGame game) {
			super();
			this.game = game;
		}
		
		@Override
		public void run(){
			List<Portfolio> portfolios = portfolioDataAccess.getPortfoliosByStockMarketGame(game.getId());
			for (Portfolio portfolio : portfolios) {
				GameStartedNotification n = new GameStartedNotification();
				n.setGame(game);
				n.setCreated(Calendar.getInstance());
				n.setUser(portfolio.getOwner());
				notificationService.addNotification(n);
			}
		}
	}
	
}
