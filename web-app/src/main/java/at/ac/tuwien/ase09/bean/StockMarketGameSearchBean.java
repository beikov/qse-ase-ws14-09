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

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;

@Named
@ViewScoped
public class StockMarketGameSearchBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private StockMarketGameDataAccess stockMarketGameAccess;
	@Inject
	private UserContext userContext;
	
	private User user;
	
	private List<StockMarketGame> games;
	private List<StockMarketGame> filteredGames;
	
	@PostConstruct
	public void init(){
		user=userContext.getUser();
		loadStockMarketGames();
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
