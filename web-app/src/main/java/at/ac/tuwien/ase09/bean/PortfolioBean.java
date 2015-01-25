package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.PortfolioContext;
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
	private String currentPortfolioName;

	@Inject
	private UserContext userContext;
	
	@Inject
	private PortfolioContext portfolioContext;
	
	public String switchPortfolio(Long id) {
		portfolioContext.setContextId(id);
		return "/portfolio/view.xhtml?faces-redirect=true&portfolioId=" + id;
	}
	
	public Long getCurrentPortfolioId() {
		return portfolioContext.getContextId();
	}
	
	public String getCurrentPortfolioName() {
		if (currentPortfolioName == null) {
			Long contextId = portfolioContext.getContextId();
			return getPortfolioName(contextId);
		}
		
		return currentPortfolioName;
	}
		
	private String getPortfolioName(Long contextId) {
		if (contextId != null) {
			for (Portfolio p : getActiveUserPortfolios()) {
				if (contextId.equals(p.getId())) {
					return p.getName();
				}
			}
		}
		
		return "Portfolios";
	}
		
	public List<Portfolio> getActiveUserPortfolios() {
		if (portfolios == null) {
			portfolios = portfolioDataAccess.getActiveUserPortfolios(userContext.getUserId());
		}
		return portfolios;
	}
}
