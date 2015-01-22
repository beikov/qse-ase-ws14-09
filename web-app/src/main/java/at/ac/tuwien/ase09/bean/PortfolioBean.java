package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;

@Named
@RequestScoped
public class PortfolioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	private List<Portfolio> portfolios;

	@Inject
	private UserContext userContext;
		
	public List<Portfolio> getActiveUserPortfolios() {
		if (portfolios == null) {
			portfolios = portfolioDataAccess.getActiveUserPortfolios(userContext.getUserId());
		}
		return portfolios;
	}
}
