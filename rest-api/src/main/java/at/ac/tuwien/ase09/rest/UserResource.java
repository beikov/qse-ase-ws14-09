package at.ac.tuwien.ase09.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import at.ac.tuwien.ase09.rest.model.PortfolioDto;

@Path("/users")
public interface UserResource {
	@GET
	@Path("/{userId}/portfolios")
	@Produces("application/json")
	public List<PortfolioDto> getPortfolios (@PathParam("userId") Long userId);
}
