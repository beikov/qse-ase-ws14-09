package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.GregorianCalendar;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.PortfolioVisibility;
import at.ac.tuwien.ase09.service.PortfolioService;

@ManagedBean
@Named
@ViewScoped
public class PortfolioCreationViewBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private Portfolio portfolio;
	private BigDecimal startCapital;
	private BigDecimal orderFee;
	private BigDecimal portfolioFee;
	private BigDecimal capitalReturnTax;

	@Inject
	UserContext userContext;

	@Inject
	private PortfolioDataAccess portfolioDataAccess;

	@Inject
	private PortfolioService portfolioService;

	@PostConstruct
	public void init() {
			portfolio = new Portfolio();
			portfolio.setVisibility(new PortfolioVisibility());
			portfolio.setSetting(new PortfolioSetting());
			orderFee = new BigDecimal(0);
			portfolioFee = new BigDecimal(0);
			capitalReturnTax = new BigDecimal(0);
	}
	

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}


	public void create(){

		if( portfolioDataAccess.existsPortfolioWithNameForUser(portfolio.getName(), userContext.getUserId()) ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Für diesen Benutzer existiert bereits ein Portfolio mit dem selben Namen.");
			FacesContext.getCurrentInstance().addMessage("createForm:name", facesMessage);
			return;
		}
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
			FacesMessage facesMessage = new FacesMessage("Fehler: Kapitalertragssteuer muss zwischen 0 und 99 % liegen!");
			FacesContext.getCurrentInstance().addMessage("createForm:capitalReturnTax", facesMessage);
			return;
		}


		portfolio.setCreated(GregorianCalendar.getInstance());
		portfolio.setCurrentCapital(new Money(startCapital, Currency.getInstance("EUR")));
		portfolio.getSetting().setStartCapital(new Money(startCapital, Currency.getInstance("EUR")));
		portfolio.getSetting().setOrderFee(new Money(orderFee, Currency.getInstance("EUR")));
		portfolio.getSetting().setPortfolioFee(new Money(portfolioFee, Currency.getInstance("EUR")));
		portfolio.getSetting().setCapitalReturnTax(capitalReturnTax);

		portfolioService.createPortfolio(portfolio);

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("list.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public BigDecimal getStartCapital() {
		return startCapital;
	}

	public void setStartCapital(BigDecimal startCapital) {
		this.startCapital = startCapital;
	}

	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	public BigDecimal getOrderFee() {
		return orderFee;
	}

	public void setOrderFee(BigDecimal orderFee) {
		this.orderFee = orderFee;
	}

	public BigDecimal getCapitalReturnTax() {
		return capitalReturnTax;
	}

	public void setCapitalReturnTax(BigDecimal capitalReturnTax) {
		this.capitalReturnTax = capitalReturnTax;
	}

	public PortfolioDataAccess getPortfolioService() {
		return portfolioDataAccess;
	}

	public void setPortfolioService(PortfolioDataAccess portfolioService) {
		this.portfolioDataAccess = portfolioService;
	}

	public BigDecimal getPortfolioFee() {
		return portfolioFee;
	}

	public void setPortfolioFee(BigDecimal portfolioFee) {
		this.portfolioFee = portfolioFee;
	}





}
