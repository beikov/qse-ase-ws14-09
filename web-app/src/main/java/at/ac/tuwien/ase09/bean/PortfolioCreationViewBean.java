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
			System.out.println("already exists");
			FacesMessage facesMessage = new FacesMessage("Error: User already has portfolio with the given name");
		      FacesContext.getCurrentInstance().addMessage("createForm:name", facesMessage);
		      return;
		}
		
		portfolio.setCreated(GregorianCalendar.getInstance());
		portfolio.setCurrentCapital(new Money(startCapital, Currency.getInstance("EUR")));
		portfolio.getSetting().setStartCapital(new Money(startCapital, Currency.getInstance("EUR")));
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

	
	
	
}
