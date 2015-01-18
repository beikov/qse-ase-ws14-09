package at.ac.tuwien.ase09.impl.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.data.ValuePaperPriceEntryDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.rest.UserResource;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;

@Stateless
public class UserResourceImpl extends AbstractResource implements UserResource {
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Inject
	private ValuePaperPriceEntryDataAccess valuePaperPriceEntryDataAccess;
	
	@Override
	public List<PortfolioDto> getPortfolios(Long userId) {
		List<Portfolio> portfolios = portfolioDataAccess.getPortfoliosByUser(userId);
		
		List<PortfolioDto> portfolioDtos = new ArrayList<>();
		for(Portfolio portfolio : portfolios){
			BigDecimal costValue = portfolioDataAccess.getCostValueForPortfolio(portfolio.getId());
			BigDecimal currentValue = portfolioDataAccess.getCurrentValueForPortfolio(portfolio.getId());
			portfolioDtos.add(new PortfolioDto(portfolio.getId(), portfolio.getName(), portfolio.getCurrentCapital().getCurrency(), portfolio.getCurrentCapital().getValue(), costValue, currentValue));
		}
		return portfolioDtos;
	}
}
