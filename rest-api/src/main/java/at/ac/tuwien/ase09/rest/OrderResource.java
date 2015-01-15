package at.ac.tuwien.ase09.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.ac.tuwien.ase09.rest.model.OrderDto;

@Path("/orders")
public interface OrderResource {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrder(OrderDto order);
}
