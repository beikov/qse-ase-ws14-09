package at.ac.tuwien.ase09.bean;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.ValuePaper;

@Named
@ViewScoped
public class StockMarketGameViewBean {

	@Inject
	private StockMarketGameDataAccess stockMarketGameAccess;
	
	private Long gameID;
	private boolean adminLoggedIn;
	private StockMarketGame stockMarketGame;
	
	public void init(){
		loadStockMarketGame(gameID);
		
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
