package at.ac.tuwien.ase09.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

@Path("/portfolios")
public interface PortfolioResource {
	
	@GET
	@Path("/{portfolioId}/valuePapers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PortfolioValuePaperDto> getValuePapers(@PathParam("portfolioId") long portfolioId);
	
	@GET
	@Produces("application/json")
	public List<PortfolioDto> getPortfolios ();
}
