package at.ac.tuwien.ase09.bean;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.GregorianCalendar;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioSetting;
import at.ac.tuwien.ase09.model.PortfolioVisibility;
import at.ac.tuwien.ase09.service.PortfolioService;

@ManagedBean
@Named
@RequestScoped
public class PortfolioCreationViewBean {
	
	private Portfolio portfolio;
	private BigDecimal startCapital;
	private BigDecimal orderFee;
	private BigDecimal portfolioFee;
	private BigDecimal capitalReturnTax;
	
	@Inject
	WebUserContext userContext;
	
	@Inject
	private PortfolioService portfolioService;
	
	@PostConstruct
	public void init() {
		portfolio = new Portfolio();
		portfolio.setVisibility(new PortfolioVisibility());
		portfolio.setSetting(new PortfolioSetting());
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
	}
	

	public void create(){
		
		if( portfolioService.existsPortfolioWithNameForUser(portfolio.getName(), userContext.getUser()) ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Für diesen Benutzer existiert bereits ein Portfolio mit dem selben Namen.");
		      FacesContext.getCurrentInstance().addMessage("createForm:name", facesMessage);
		      return;
		}
		if( startCapital.compareTo(new BigDecimal(0)) == -1 ){
			FacesMessage facesMessage = new FacesMessage("Fehler: Startkapital muss größer gleich 0 (0 für unendliches Kapital) sein!");
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
		
		portfolio.setOwner(userContext.getUser());
		
		portfolioService.savePortfolio(portfolio);
		
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

	public WebUserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(WebUserContext userContext) {
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

	public PortfolioService getPortfolioService() {
		return portfolioService;
	}

	public void setPortfolioService(PortfolioService portfolioService) {
		this.portfolioService = portfolioService;
	}

	public BigDecimal getPortfolioFee() {
		return portfolioFee;
	}

	public void setPortfolioFee(BigDecimal portfolioFee) {
		this.portfolioFee = portfolioFee;
	}

	
	
	
	
}
