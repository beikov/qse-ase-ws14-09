package at.ac.tuwien.ase09.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import at.ac.tuwien.ase09.context.PortfolioContext;
import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.exception.AppException;
import at.ac.tuwien.ase09.exception.EntityNotFoundException;
import at.ac.tuwien.ase09.model.Money;
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
	
	public String getCapital(Portfolio p, Money m) {
		if (p.getSetting().getStartCapital().getValue().compareTo(BigDecimal.ZERO) == 0) {
			return "unbegrenzt";
		}
		return m.toString();
	}
	
	public Money getCostValueForPortfolio(Long portfolioId) {
		return null;
	}
	
	public Money getCurrentValueForPortfolio(Long portfolioId) {
		return null;
	}
	
	public BigDecimal getPortfolioPerformance(Long portfolioId) {
		return portfolioDataAccess.getPortfolioPerformance(portfolioId);
	}
}
