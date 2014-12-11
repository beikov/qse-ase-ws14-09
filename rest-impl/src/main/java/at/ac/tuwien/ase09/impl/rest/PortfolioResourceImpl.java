package at.ac.tuwien.ase09.impl.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.PortfolioDataAccess;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.model.User;
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;

@Stateless
public class PortfolioResourceImpl implements PortfolioResource {
	
	@Inject
	private PortfolioDataAccess portfolioDataAccess;
	
	@Override
	public List<PortfolioDto> getPortfolios(Long userId) {
		User user = new User();
		user.setId(userId);
		List<Portfolio> portfolios = portfolioDataAccess.getPortfoliosByUser(user);
		return portfolios.stream().map(portfolio -> new PortfolioDto(portfolio.getName(), portfolio.getCurrentCapital())).collect(Collectors.toList());
	}
}
