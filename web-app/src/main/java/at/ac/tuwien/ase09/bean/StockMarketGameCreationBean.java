package at.ac.tuwien.ase09.bean;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.StockMarketGameDataAccess;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.PortfolioVisibility;
import at.ac.tuwien.ase09.model.StockMarketGame;

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

	private Long stockMarketGameId;

	private StockMarketGame stockMarketGame;

	private BigDecimal startCapital;
	private BigDecimal orderFee;
	private BigDecimal portfolioFee;
	private BigDecimal capitalReturnTax;



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

	@PostConstruct
	public void init() {

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

	public void createStockMarketGame(){

		if( startCapital == null){
			startCapital = new BigDecimal(0);
		}else if( startCapital.compareTo(new BigDecimal(0)) == -1 ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Startkapital muss größer als 0 sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:startCapital", facesMessage);
			return;
		}

		if( orderFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Ordergebühr muss größer gleich 0  sein!");
			FacesContext.getCurrentInstance().addMessage("createForm:orderFee", facesMessage);
			return;
		}

		if( portfolioFee.compareTo(new BigDecimal(0)) == -1){
			FacesMessage facesMessage = new FacesMessage("Fehler: Portfoliogebühr muss größer gleich 0  sein!");
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
		
		stockMarketGame.getSetting().setStartCapital(new Money(startCapital, Currency.getInstance("EUR")));
		stockMarketGame.getSetting().setOrderFee(new Money(orderFee, Currency.getInstance("EUR")));
		stockMarketGame.getSetting().setPortfolioFee(new Money(portfolioFee, Currency.getInstance("EUR")));
		stockMarketGame.getSetting().setCapitalReturnTax(capitalReturnTax);

		//portfolioService.savePortfolio(portfolio);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("list.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
