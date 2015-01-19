package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.model.User;

@Named
@ViewScoped
public class StockMarketGameRankingBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private WebUserContext userContext;

	@Inject
	private StockMarketGameDataAccess stockMarketGameDataAccess;

	@Inject
	private InstitutionDataAccess institutionDataAccess;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;

	private Long stockMarketGameId;

	private StockMarketGame stockMarketGame;

	private User loggedInUser;
	private Institution userInstitution;

	private List<Portfolio> portfolioRankingList;
	
	

	public List<Portfolio> getPortfolioRankingList() {
		return portfolioRankingList;
	}
	public void setPortfolioRankingList(List<Portfolio> portfolioRankingList) {
		this.portfolioRankingList = portfolioRankingList;
	}
	public Long getStockMarketGameId() {
		return stockMarketGameId;
	}
	public void setStockMarketGameId(Long stockMarketGameId) {
		this.stockMarketGameId = stockMarketGameId;
	}
	public StockMarketGame getStockMarketGame() {
		return stockMarketGame;
	}
	public void setStockMarketGame(StockMarketGame stockMarketGame) {
		this.stockMarketGame = stockMarketGame;
	}
	public User getLoggedInUser() {
		return loggedInUser;
	}
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
	public Institution getUserInstitution() {
		return userInstitution;
	}
	public void setUserInstitution(Institution userInstitution) {
		this.userInstitution = userInstitution;
	}



	public void init() throws IOException {

		loggedInUser = userContext.getUser();

		if(loggedInUser != null){
			try{
				userInstitution = institutionDataAccess.getByAdmin(loggedInUser.getUsername());
			}
			catch(EntityNotFoundException e){}
		}

		loadStockMarketGame();
		
		loadStockMarketGameRanking();
		
		if(!isStockMarketGameAdmin() && !isStockMarketGameParticipant()){
			FacesContext.getCurrentInstance().getExternalContext().responseSendError(403, "Nur die Teilnehmer und der Ersteller dieses Börsenspiels können das Teilnehmerranking aufrufen");
			FacesContext.getCurrentInstance().responseComplete();
		}

	}

	public boolean isStockMarketGameAdmin(){
		if(stockMarketGame != null && stockMarketGame.getOwner() != null && userInstitution != null){
			return stockMarketGame.getOwner().getId() == userInstitution.getId();
		}
		return false;
	}
	public boolean isStockMarketGameParticipant(){
		if(stockMarketGame != null && loggedInUser != null){
			
			try{			
				return portfolioDataAccess.getByGameAndUser(stockMarketGame, loggedInUser) != null;
			}
			catch(EntityNotFoundException e){
				return false;
			}

		}
		return false;
	}

	public void loadStockMarketGame() throws IOException{
		if(stockMarketGameId != null){
			try{
				stockMarketGame = stockMarketGameDataAccess.getStockMarketGameByID(stockMarketGameId);
			}
			catch(EntityNotFoundException e){
				FacesContext.getCurrentInstance().getExternalContext().responseSendError(404, "Das Börsenspiel mit der Id '" + stockMarketGameId + "' konnte nicht gefunden werden");
				FacesContext.getCurrentInstance().responseComplete();
			}
		}
	}
	private void loadStockMarketGameRanking() {
		if(stockMarketGame != null){
			
			portfolioRankingList = new ArrayList<Portfolio>();
			
			portfolioRankingList = portfolioDataAccess.getPortfoliosByStockMarketGame(stockMarketGame.getId());
			
			Collections.sort(portfolioRankingList, new Comparator<Portfolio>() {
		        @Override
		        public int compare(Portfolio p1, Portfolio p2)
		        {
		        	BigDecimal p1Sum = p1.getSetting().getStartCapital().getValue();
		        	BigDecimal p2Sum = p2.getSetting().getStartCapital().getValue();

		        	BigDecimal p1Value = portfolioDataAccess.getCurrentValueForPortfolio(p1.getId());
		        	BigDecimal p2Value = portfolioDataAccess.getCurrentValueForPortfolio(p2.getId());

		        	if(p1Value != null){
		        		p1Sum.add(p1Value);
		        	}
		        	
		        	if(p2Value != null){
		        		p2Sum.add(p2Value);
		        	}

		        	return p1Sum.subtract(p2Sum).intValue();
		        }
		    });
		}
	}

}
