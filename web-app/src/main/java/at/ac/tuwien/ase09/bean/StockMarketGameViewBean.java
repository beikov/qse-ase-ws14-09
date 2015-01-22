package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
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
	private WebUserContext userContext;
	
	private Long gameID;
	private boolean adminLoggedIn;
	private StockMarketGame stockMarketGame=null;
	
	private Map<String,String> mainGameAttributes=null;
	private List<String> attributekeys = null;
	private Set<ValuePaper> allowedPapers=null;
	
	
	public void init(){

			loadStockMarketGame(gameID);
			if(stockMarketGame!=null) {
				adminLoggedIn = stockMarketGame.getOwner().getId().equals(userContext.getUserId());
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
	
	private void loadStockMarketGame(Long gameID) {

		try{
			this.stockMarketGame = stockMarketGameAccess.getStockMarketGameByID(gameID);
		}
		catch(EntityNotFoundException e){
			this.stockMarketGame = null;
			/*
			try {
				FacesContext.getCurrentInstance().getExternalContext().responseSendError(404, "Das B�rsenspiel mit der Id '" + gameID + "' konnte nicht gefunden werden");
			} catch (IOException e1) {
				
			}
			FacesContext.getCurrentInstance().responseComplete();
			*/

		}

	}
	public String getStartcapital()
	{
		if(stockMarketGame!=null)
		{
			if(stockMarketGame.getSetting().getStartCapital().getValue().compareTo(new BigDecimal(0.00))==0)
			{
				return "unbegrenzt";
			}
			else
			{
				return stockMarketGame.getSetting().getStartCapital().toString();
			}
		}
		else
		{
			return null;
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
	
	public Long getSubscribedUsersCount(StockMarketGame game) {
		return stockMarketGameAccess.getSubscribedUsersCount(game);
	}
}
