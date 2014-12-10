package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;

@Named
@ViewScoped
public class PortfolioBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	public List<Portfolio> getPortfolios() {
		return portfolioDataAccess.getPortfolios();
	}

	public List<Portfolio> getUserPortfolios(User user) {
		return portfolioDataAccess.getPortfoliosByUser(user);
	}	
	
}
