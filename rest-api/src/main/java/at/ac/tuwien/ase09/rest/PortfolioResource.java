package at.ac.tuwien.ase09.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import at.ac.tuwien.ase09.model.Portfolio;


public interface PortfolioResource {
	@GET
	@Path("/portfolios/{userId}")
	@Produces("application/json")
	public Portfolio getPortfolio (@PathParam("userId") Long userId);
}
