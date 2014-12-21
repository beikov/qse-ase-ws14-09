package at.ac.tuwien.ase09.bean;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
	
	private Map<String,String> mainGameAttributes=null;
	private Set<ValuePaper> allowedPapers=null;
	
	public void init(){
		User user=userContext.getUser();
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


	public Map<String, String> getMainGameAttributes() {
		return mainGameAttributes;
	}


	public void setMainGameAttributes(Map<String, String> mainGameAttributes) {
		this.mainGameAttributes = mainGameAttributes;
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
		this.allowedPapers=new HashSet<ValuePaper>();
		
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");	

		if(stockMarketGame.getAllowedValuePapers()!=null)
		{
			this.allowedPapers=stockMarketGame.getAllowedValuePapers();
		}
		if(stockMarketGame.getName() != null){
			this.mainGameAttributes.put("Bezeichnung: ", stockMarketGame.getName());
		}

		if(stockMarketGame.getOwner().getName() != null){
			this.mainGameAttributes.put("Ersteller: ", stockMarketGame.getOwner().getName());
		}
		
		if(stockMarketGame.getRegistrationFrom()!=null)
		{
			this.mainGameAttributes.put("Anmeldestart: ", format.format(stockMarketGame.getRegistrationFrom().getTime()));
		}
		
		if(stockMarketGame.getRegistrationTo()!=null)
		{
			this.mainGameAttributes.put("Anmeldeende: ", format.format(stockMarketGame.getRegistrationTo().getTime()));
		}
		
		if(stockMarketGame.getValidFrom()!=null)
		{
			this.mainGameAttributes.put("Startdatum: ", format.format(stockMarketGame.getValidFrom().getTime()));
		}
		
		if(stockMarketGame.getValidTo()!=null)
		{
			this.mainGameAttributes.put("Enddatum: ", format.format(stockMarketGame.getValidTo().getTime()));
		}

		if(stockMarketGame.getSetting().getStartCapital() != null){
			this.mainGameAttributes.put("Startkapital: ", stockMarketGame.getSetting().getStartCapital()+""+stockMarketGame.getSetting().getStartCapital().getCurrency().getSymbol());
		}
		
		if(stockMarketGame.getSetting().getPortfolioFee() != null){
			this.mainGameAttributes.put("Portfoliospesen: ", stockMarketGame.getSetting().getPortfolioFee()+""+stockMarketGame.getSetting().getPortfolioFee().getCurrency().getSymbol());
		}
		
		if(stockMarketGame.getSetting().getOrderFee() != null){
			this.mainGameAttributes.put("Orderspesen: ", stockMarketGame.getSetting().getOrderFee()+""+stockMarketGame.getSetting().getOrderFee().getCurrency().getSymbol());
		}
		
		if(stockMarketGame.getSetting().getCapitalReturnTax()!=null)
		{
			this.mainGameAttributes.put("Kapitalertragssteuer: ", stockMarketGame.getSetting().getCapitalReturnTax()+"%");
		}
	}
}
