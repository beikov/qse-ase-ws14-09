package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.context.WebUserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Named
@ViewScoped
public class PortfolioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	private List<Portfolio> portfolios;

	@Inject
	private UserContext userContext;
	
	/*public List<Portfolio> getPortfolios() {
		return portfolioDataAccess.getPortfolios();
	}*/

	public List<Portfolio> getUserPortfolios() {
		// only for /protected/portfolio/list.xhtml
		User user = userContext.getUser();
		return portfolioDataAccess.getPortfoliosByUser(user.getId());
	}	
	
	public List<Portfolio> getActiveUserPortfolios(User user) {
		if (portfolios == null) {
			portfolios = portfolioDataAccess.getActiveUserPortfolios(user);
		}
		return portfolios;
	}
}
