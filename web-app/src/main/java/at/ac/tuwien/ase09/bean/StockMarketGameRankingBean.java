package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserAccount;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.InstitutionDataAccess;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.data.UserDataAccess;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Institution;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.StockMarketGame;

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
	
	@Inject
	private UserDataAccess userDataAccess;

	private Long stockMarketGameId;

	private StockMarketGame stockMarketGame;

	private UserAccount loggedInUser;
	private Institution userInstitution;

	private List<Portfolio> portfolioRankingList;
	private Map<Long, Money> portfolioRankingMap;

	private Map<Currency, BigDecimal> conversionRateMap = new HashMap<>();

	
	
	public Map<Long, Money> getPortfolioRankingMap() {
		return portfolioRankingMap;
	}
	public void setPortfolioRankingMap(Map<Long, Money> portfolioRankingMap) {
		this.portfolioRankingMap = portfolioRankingMap;
	}
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

		if(stockMarketGame == null){
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().responseSendError(404, "Das Börsenspiel konnte nicht gefunden werden.");
			context.responseComplete();
			return;
		}

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
				return portfolioDataAccess.getByGameAndUser(stockMarketGame, loggedInUser.getId()) != null;
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
			catch(EntityNotFoundException e){}
		}
	}
	private void loadStockMarketGameRanking() {
		if(stockMarketGame != null){

			portfolioRankingList = new ArrayList<Portfolio>();
			portfolioRankingMap = new HashMap<Long, Money>();


			portfolioRankingList = portfolioDataAccess.getPortfoliosByStockMarketGame(stockMarketGame.getId());


			for(Portfolio p : portfolioRankingList){
				
				p = portfolioDataAccess.getPortfolioById(p.getId());
				
				conversionRateMap = portfolioDataAccess.getConversionRateMapForPortfolio(p);
								
				Money sum = createMoney(p.getCurrentCapital().getValue(), p.getCurrentCapital().getCurrency());

				BigDecimal currentValue = portfolioDataAccess.getCurrentValueForPortfolio(p.getId(), conversionRateMap);
				
				Money currentValueForPortfolio;

				if(currentValue != null){
					currentValueForPortfolio = createMoney(currentValue, p.getCurrentCapital().getCurrency());
					sum.setValue(sum.getValue().add(currentValueForPortfolio.getValue()));
				}

				portfolioRankingMap.put(p.getId(), sum);
			}

			Collections.sort(portfolioRankingList, new Comparator<Portfolio>() {
				@Override
				public int compare(Portfolio p1, Portfolio p2)
				{
					return portfolioRankingMap.get(p2.getId()).getValue().subtract(portfolioRankingMap.get(p1.getId()).getValue()).intValue();
				}
			});
		}
	}
	
	public String getEmailByUsername(String username){
		return userDataAccess.getEmailByUsername(username);
	}
	
    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {
        Document pdf = (Document) document;
        pdf.open();
        pdf.setPageSize(PageSize.A4);
        
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        
        PdfPTable table = new PdfPTable(2);
        
        PdfPCell cell = new PdfPCell(new Phrase("Börsenspiel:"));
        cell.setBorder(0);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase(stockMarketGame.getName()));
        cell.setBorder(0);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Börsenspielbeginn:"));
        cell.setBorder(0);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(format.format(stockMarketGame.getValidFrom().getTime())));
        cell.setBorder(0);
        table.addCell(cell);
        
        cell = new PdfPCell(new Phrase("Börsenspielende:"));
        cell.setBorder(0);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(format.format(stockMarketGame.getValidTo().getTime())));
        cell.setBorder(0);
        table.addCell(cell);
        
        table.setSpacingAfter(30);
        table.addCell(cell);


        pdf.add(table);
    }
    
	private Money createMoney(BigDecimal value, String currencyCode) {
		Money m = new Money();
		m.setCurrency(Currency.getInstance(currencyCode));
		if (value == null)
			value = new BigDecimal(0);
		m.setValue(value);
		return m;
		
	}
	
	private Money createMoney(BigDecimal value, Currency currency) {
		return createMoney(value, currency.getCurrencyCode());
	}

}
