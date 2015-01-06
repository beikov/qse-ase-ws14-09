package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.service.PortfolioService;
import at.ac.tuwien.ase09.service.StockMarketGameService;

@Named
@ViewScoped
public class StockMarketGameSearchBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private StockMarketGameDataAccess stockMarketGameAccess;
	@Inject
	private StockMarketGameService stockMarketGameService;
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	@Inject
	private PortfolioService portfolioService;
	@Inject
	private UserContext userContext;
	
	private User user;
	
	private List<StockMarketGame> games;
	private List<StockMarketGame> filteredGames;
	private StockMarketGame selectedGame;
	
	@PostConstruct
	public void init(){
		user=userContext.getUser();
		loadStockMarketGames();
	}
	
	public boolean isParticipatingInGame(StockMarketGame game) {
		if (game == null) {
			return false;
		}
		try {
			portfolioDataAccess.getByGameAndUser(game, user);
			return true;
		} catch(EntityNotFoundException e) {
			return false;
		}
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<StockMarketGame> getGames() {
		return games;
	}
	
	public List<StockMarketGame> getFilteredGames() {
		return filteredGames;
	}

	public StockMarketGame getSelectedGame() {
		return selectedGame;
	}
	
	public void setSelectedGame(StockMarketGame selectedGame) {
		this.selectedGame = selectedGame;
	}
	
	public void onRowSelect(SelectEvent event) {
    }
 
    public void onRowUnselect(UnselectEvent event) {
    }
	
    public void handleClose(CloseEvent event) {
    	selectedGame = null;
    }
    
    public boolean isAjaxRequest() {
        return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
    }
    
    public void participateInGame() {
    	try {
    		portfolioDataAccess.getByGameAndUser(selectedGame, user);
    	} catch(EntityNotFoundException e) {
    		stockMarketGameService.participateInGame(selectedGame, user);
        	FacesMessage message = new FacesMessage("Sie nehmen nun an dem Börsenspiel '" + selectedGame.getName() + "' teil");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
    	}
    	FacesMessage message = new FacesMessage("Sie nehmen bereits an dem Börsenspiel '" + selectedGame.getName() + "' teil");
        FacesContext.getCurrentInstance().addMessage(null, message);
    	
    }
    
    public void unsubscribeFromGame() {
    	Portfolio p;
    	try {
    		p = portfolioDataAccess.getByGameAndUser(selectedGame, user);
    		portfolioService.removePortfolio(p);
    		FacesMessage message = new FacesMessage("Erfolgreich vom Börsenspiel '" + selectedGame.getName() + "' abgemeldet");
            FacesContext.getCurrentInstance().addMessage(null, message);
    		return;
    	} catch(EntityNotFoundException e) {
    	}
    }
    
	private void loadStockMarketGames() {
		try {
			games = stockMarketGameAccess.getStockMargetGames();
		} catch(AppException e) {
			FacesMessage message = new FacesMessage("Fehler beim Laden der Börsenspiele");
	        FacesContext.getCurrentInstance().addMessage(null, message);
	        games = new ArrayList<>();
		}

	}
}
