package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;

@Named
@ViewScoped
public class StockMarketGameViewBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private StockMarketGameDataAccess stockMarketGameAccess;
	@Inject
	private UserContext userContext;
	
	private Long gameID;
	private boolean adminLoggedIn;
	private User user;
	private StockMarketGame stockMarketGame;
	
	private Map<String,String> mainGameAttributes=null;
	
	public void init(){
		user=userContext.getUser();
		loadStockMarketGame(gameID);
		checkAdminLoggedIn();
		
		if(stockMarketGame!=null)
		{
			loadGameAttributes();
		}
		
	}
	
	
	public Long getGameID() {
		return gameID;
	}


	public void setGameID(Long gameID) {
		this.gameID = gameID;
	}


	public boolean isAdminLoggedIn() {
		return adminLoggedIn;
	}


	public void setAdminLoggedIn(boolean adminLoggedIn) {
		this.adminLoggedIn = adminLoggedIn;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public StockMarketGame getStockMarketGame() {
		return stockMarketGame;
	}


	public void setStockMarketGame(StockMarketGame stockMarketGame) {
		this.stockMarketGame = stockMarketGame;
	}


	private void checkAdminLoggedIn() {
		if(stockMarketGame!=null)
			adminLoggedIn=user.equals(stockMarketGame.getOwner().getAdmin());
		
	}
	private void loadStockMarketGame(Long gameID) {

		try{
			this.stockMarketGame = stockMarketGameAccess.getStockMarketGameByID(gameID);
		}
		catch(EntityNotFoundException e){
			this.stockMarketGame = null;
		}

	}
	private void loadGameAttributes(){
		
		this.mainGameAttributes = new LinkedHashMap<String, String>();
		//this.additionalValuePaperAttributes = new LinkedHashMap<String, String>();

		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");	

		if(stockMarketGame.getName() != null){
			this.mainGameAttributes.put("Bezeichnung: ", stockMarketGame.getName());
		}

		if(stockMarketGame.getOwner().getName() != null){
			this.mainGameAttributes.put("Ersteller: ", stockMarketGame.getOwner().getName());
		}

		
	}
}
