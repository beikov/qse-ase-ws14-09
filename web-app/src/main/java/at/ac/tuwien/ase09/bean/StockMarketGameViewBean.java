package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewScoped;
import javax.faces.view.facelets.FaceletContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.model.ValuePaper;

@Named
@SessionScoped
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
	private List<String> attributekeys = null;
	private Set<ValuePaper> allowedPapers=null;
	
	
	public void init(){
		FaceletContext faceletContext = (FaceletContext) FacesContext
				.getCurrentInstance().getAttributes()
				.get(FaceletContext.FACELET_CONTEXT_KEY);
		gameID = (Long) faceletContext.getAttribute("gameId");
		// System.out.println(gameID);
		// gameID=Long.parseLong(gameId);

		user = userContext.getUser();
		loadStockMarketGame(gameID);
		checkAdminLoggedIn();

		if (stockMarketGame != null) {
			loadGameAttributes();
			attributekeys=new ArrayList<String>();
			for (String key : mainGameAttributes.keySet()) {
				attributekeys.add(key);
			}

		}
		
	}
	
	
	public List<String> getAttributekeys() {
		return attributekeys;
	}


	public void setAttributekeys(List<String> attributekeys) {
		this.attributekeys = attributekeys;
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
			this.mainGameAttributes.put("Startkapital: ", stockMarketGame.getSetting().getStartCapital()+"");
		}
		
		if(stockMarketGame.getSetting().getPortfolioFee() != null){
			this.mainGameAttributes.put("Portfoliospesen: ", stockMarketGame.getSetting().getPortfolioFee()+"");
		}
		
		if(stockMarketGame.getSetting().getOrderFee() != null){
			this.mainGameAttributes.put("Orderspesen: ", stockMarketGame.getSetting().getOrderFee()+"");
		}
		
		if(stockMarketGame.getSetting().getCapitalReturnTax()!=null)
		{
			this.mainGameAttributes.put("Kapitalertragssteuer: ", stockMarketGame.getSetting().getCapitalReturnTax()+"%");
		}
	}
	
	public StreamedContent getImage() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        }
        else {
        	String gameID = context.getExternalContext().getRequestParameterMap().get("gameIDLogo");
        	StockMarketGame s = stockMarketGameAccess.getStockMarketGameByID(Long.parseLong(gameID));
        	
        	if(s.getLogo()!=null)
        	{
				try {
					return new DefaultStreamedContent(s.getLogo().getBinaryStream());
				} catch (SQLException e) {
					e.printStackTrace();
					throw new AppException(e.getMessage());
				}
			}
        	else
        	{
        		 return new DefaultStreamedContent();
        	}
        }
    }
}
