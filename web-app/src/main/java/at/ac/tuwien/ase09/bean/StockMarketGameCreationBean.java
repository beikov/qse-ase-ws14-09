package at.ac.tuwien.ase09.bean;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Calendar;
import java.util.Currency;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.StockMarketGame;
import at.ac.tuwien.ase09.service.StockMarketGameService;

import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class StockMarketGameCreationBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private WebUserContext userContext;
	
	@Inject
	private StockMarketGameDataAccess stockMarketGameDataAccess;
	
	@Inject
	private StockMarketGameService stockMarketGameService;

	private Long stockMarketGameId;

	private StockMarketGame stockMarketGame;

	private String name;
	private Calendar validFrom;
	private Calendar validTo;
	private Calendar registrationFrom;
	private Calendar registrationTo;
	private String text;
	private Blob logo;
	
	private BigDecimal startCapital;
	private BigDecimal orderFee;
	private BigDecimal portfolioFee;
	private BigDecimal capitalReturnTax;



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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Calendar getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}
	public Calendar getValidTo() {
		return validTo;
	}
	public void setValidTo(Calendar validTo) {
		this.validTo = validTo;
	}
	public Calendar getRegistrationFrom() {
		return registrationFrom;
	}
	public void setRegistrationFrom(Calendar registrationFrom) {
		this.registrationFrom = registrationFrom;
	}
	public Calendar getRegistrationTo() {
		return registrationTo;
	}
	public void setRegistrationTo(Calendar registrationTo) {
		this.registrationTo = registrationTo;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Blob getLogo() {
		return logo;
	}
	public void setLogo(Blob logo) {
		this.logo = logo;
	}
	public BigDecimal getStartCapital() {
		return startCapital;
	}
	public void setStartCapital(BigDecimal startCapital) {
		this.startCapital = startCapital;
	}
	public BigDecimal getOrderFee() {
		return orderFee;
	}
	public void setOrderFee(BigDecimal orderFee) {
		this.orderFee = orderFee;
	}
	public BigDecimal getPortfolioFee() {
		return portfolioFee;
	}
	public void setPortfolioFee(BigDecimal portfolioFee) {
		this.portfolioFee = portfolioFee;
	}
	public BigDecimal getCapitalReturnTax() {
		return capitalReturnTax;
	}
	public void setCapitalReturnTax(BigDecimal capitalReturnTax) {
		this.capitalReturnTax = capitalReturnTax;
	}

	public void init() {

		loadStockMarketGame();
		
		if(stockMarketGame != null){
			orderFee = stockMarketGame.getSetting().getOrderFee().getValue();
			portfolioFee = stockMarketGame.getSetting().getPortfolioFee().getValue();
			capitalReturnTax = stockMarketGame.getSetting().getCapitalReturnTax();
		}
		else{
			orderFee = new BigDecimal(0);
			portfolioFee = new BigDecimal(0);
			capitalReturnTax = new BigDecimal(0);
		}
	}
	
	public void loadStockMarketGame(){
		if(stockMarketGameId != null){
			stockMarketGame = stockMarketGameDataAccess.getStockMarketGameByID(stockMarketGameId);
		}
	}

	public void createStockMarketGame(){

		if( startCapital == null){
			startCapital = new BigDecimal(0);
		}else if( startCapital.compareTo(new BigDecimal(0)) == -1 ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Startkapital muss gr��er als 0 sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:startCapital", facesMessage);
			return;
		}

		if( orderFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Ordergeb�hr muss gr��er gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:orderFee", facesMessage);
			return;
		}

		if( portfolioFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Portfoliogeb�hr muss gr��er gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:portfolioFee", facesMessage);
			return;
		}

		if( capitalReturnTax.compareTo(new BigDecimal(0)) == -1 || capitalReturnTax.compareTo(new BigDecimal(99)) == 1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Kapitalertragssteuer muss zwischen 0% und 99% liegen!");
			FacesContext.getCurrentInstance().addMessage("createForm:capitalReturnTax", facesMessage);
			return;
		}

		if(stockMarketGame != null){
			stockMarketGame = new StockMarketGame();
		}
		
		stockMarketGame.setName(name);
		stockMarketGame.setText(text);
		stockMarketGame.setValidFrom(validFrom);
		stockMarketGame.setValidTo(validTo);
		stockMarketGame.setRegistrationFrom(registrationFrom);
		stockMarketGame.setRegistrationTo(registrationTo);
		stockMarketGame.getSetting().setStartCapital(new Money(startCapital, Currency.getInstance("EUR")));
		stockMarketGame.getSetting().setOrderFee(new Money(orderFee, Currency.getInstance("EUR")));
		stockMarketGame.getSetting().setPortfolioFee(new Money(portfolioFee, Currency.getInstance("EUR")));
		stockMarketGame.getSetting().setCapitalReturnTax(capitalReturnTax);

		stockMarketGameService.saveStockMarketGame(stockMarketGame);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("list.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
