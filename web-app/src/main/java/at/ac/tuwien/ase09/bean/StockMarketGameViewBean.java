package at.ac.tuwien.ase09.bean;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;

@Named
@ViewScoped
public class StockMarketGameViewBean {

	@Inject
	private StockMarketGameDataAccess stockMarketGameAccess;
	@Inject
	private WebUserContext userContext;
	
	private Long gameID;
	private boolean adminLoggedIn;
	private User user;
	private StockMarketGame stockMarketGame;
	
	public void init(){
		User user=userContext.getUser();
		loadStockMarketGame(gameID);
		checkAdminLoggedIn();
		
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
}
