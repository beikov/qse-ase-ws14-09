package at.ac.tuwien.ase09.impl.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.context.UserContext;
import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;

@Stateless
public class PortfolioResourceImpl extends AbstractResource implements PortfolioResource {

	@Inject
	private ValuePaperDataAccess valuePaperDataAcess;
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private UserContext userContext;
	
	@Override
	public List<PortfolioValuePaperDto> getValuePapers(long portfolioId) {
		List<PortfolioValuePaper> portfolioValuePapers = valuePaperDataAcess.getValuePapersForPortfolio(portfolioId);
		List<PortfolioValuePaperDto> results = new ArrayList<>();
		
		for(PortfolioValuePaper portfolioValuePaper : portfolioValuePapers){
			results.add(createFromEntity(portfolioValuePaper));
		}
		
		return results;
	}
	@Override
	public List<PortfolioDto> getPortfolios() {
		List<Portfolio> portfolios = portfolioDataAccess.getPortfoliosByUser(userContext.getUser().getId());
		
		List<PortfolioDto> portfolioDtos = new ArrayList<>();
		for(Portfolio portfolio : portfolios){
			BigDecimal costValue = portfolioDataAccess.getCostValueForPortfolio(portfolio.getId());
			BigDecimal currentValue = portfolioDataAccess.getCurrentValueForPortfolio(portfolio.getId());
			portfolioDtos.add(new PortfolioDto(portfolio.getId(), portfolio.getName(), portfolio.getCurrentCapital().getCurrency(), portfolio.getCurrentCapital().getValue(), costValue, currentValue, portfolio.getSetting().getOrderFee().getValue()));
		}
		return portfolioDtos;
	}
	
	
}
