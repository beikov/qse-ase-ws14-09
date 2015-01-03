package at.ac.tuwien.ase09.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.rest.model.ValuePaperDto;

@Path("/valuePapers")
public interface ValuePaperResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ValuePaperDto> getValuePapers (@QueryParam("filter") String filter, @QueryParam("type") ValuePaperType valuePaperType);
}
