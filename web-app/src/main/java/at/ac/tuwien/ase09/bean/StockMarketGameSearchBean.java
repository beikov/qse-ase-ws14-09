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
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
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
	
	private String filterGameName;
	private String filterGameText;
	private String filterGameInstitutionName;
	
	private List<StockMarketGame> games;
	//private List<StockMarketGame> filteredGames;
	//private StockMarketGame selectedGame;
	
	@PostConstruct
	public void init() {
		loadStockMarketGames();
	}
	
	public boolean isParticipatingInGame(StockMarketGame game) {
		System.out.println(game);
		if (game == null) {
			return false;
		}
		try {
			portfolioDataAccess.getByGameAndUser(game, userContext.getUserId());
			return true;
		} catch(EntityNotFoundException e) {
			return false;
		}
	}
	
	public String getParticipateButtonText(StockMarketGame game) {
		if (isParticipatingInGame(game)) {
			return "Abmelden";
		}
		return "Teilnehmen";
	}
	
	public String getFilterGameName() {
		return filterGameName;
	}

	public void setFilterGameName(String filterGameName) {
		this.filterGameName = filterGameName;
	}

	public String getFilterGameText() {
		return filterGameText;
	}

	public void setFilterGameText(String filterGameText) {
		this.filterGameText = filterGameText;
	}

	public String getFilterGameInstitutionName() {
		return filterGameInstitutionName;
	}

	public void setFilterGameInstitutionName(String filterGameInstitutionName) {
		this.filterGameInstitutionName = filterGameInstitutionName;
	}
	
	public List<StockMarketGame> getGames() {
		return games;
	}
	
	/*public List<StockMarketGame> getFilteredGames() {
		return filteredGames;
	}

	public StockMarketGame getSelectedGame() {
		return selectedGame;
	}
	
	public void setSelectedGame(StockMarketGame selectedGame) {
		this.selectedGame = selectedGame;
	}*/
	
	public void onRowSelect(SelectEvent event) {
    }
 
    public void onRowUnselect(UnselectEvent event) {
    }
	
    /*public void handleClose(CloseEvent event) {
    	selectedGame = null;
    }*/
    
    public void handleFilterGameKeyEvent() {
    	loadStockMarketGames();
    }
    
    public boolean isAjaxRequest() {
        return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
    }
    
    public void participate(StockMarketGame game) {
    	if (isParticipatingInGame(game)) {
    		unsubscribeFromGame(game);
    	} else {
    		subscribeForGame(game);
    	}
    }
    
    public void subscribeForGame(StockMarketGame game) {
    	try {
    		portfolioDataAccess.getByGameAndUser(game, userContext.getUserId());
    	} catch(EntityNotFoundException e) {
    		stockMarketGameService.participateInGame(game, userContext.getUserId());
        	FacesMessage message = new FacesMessage("Sie nehmen nun am Börsenspiel '" + game.getName() + "' teil");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
    	}
    	FacesMessage message = new FacesMessage("Sie nehmen bereits am Börsenspiel '" + game.getName() + "' teil");
        FacesContext.getCurrentInstance().addMessage(null, message);
    	
    }
    
    public void unsubscribeFromGame(StockMarketGame game) {
    	Portfolio p;
    	try {
    		p = portfolioDataAccess.getByGameAndUser(game, userContext.getUserId());
    		portfolioService.removePortfolio(p);
    		FacesMessage message = new FacesMessage("Erfolgreich vom Börsenspiel '" + game.getName() + "' abgemeldet");
            FacesContext.getCurrentInstance().addMessage(null, message);
    		return;
    	} catch(EntityNotFoundException e) {
    	}
    }
    
	private void loadStockMarketGames() {
		try {
			//games = stockMarketGameAccess.getStockMargetGames();
			
			/*Institution institution = new Institution();
			institution.setName(filterGameInstitutionName);
			StockMarketGame searchExample = new StockMarketGame();
			searchExample.setName(filterGameName);
			searchExample.setText(filterGameText);
			searchExample.setOwner(institution);*/
			
			games = stockMarketGameAccess.findByNameTextOwner(filterGameName, filterGameText, filterGameInstitutionName);
			//System.out.println(games);
		} catch(AppException e) {
			FacesMessage message = new FacesMessage("Fehler beim Laden der Börsenspiele");
	        FacesContext.getCurrentInstance().addMessage(null, message);
	        games = new ArrayList<>();
		}

	}
}
