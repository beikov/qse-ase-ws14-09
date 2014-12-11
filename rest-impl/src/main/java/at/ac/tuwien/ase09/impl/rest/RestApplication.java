package at.ac.tuwien.ase09.impl.rest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import at.ac.tuwien.ase09.model.order.Order;
import at.ac.tuwien.ase09.model.Portfolio;


@ApplicationPath("/api")
public class RestApplication extends Application {
	
	@Inject
	private EntityManager em;
	
	@GET
	@Path("/test/{name}")
	@Produces("application/json")
	public Portfolio getPortfolio (@PathParam("name") String name) {
		
		Portfolio portfolio = new Portfolio();
		portfolio.setName(name);
		//getPortfolioById(portfolio_id);
		return portfolio;
	}
	/*
	public Portfolio getPortfolioById(Long id) {
        try {
                return em.createQuery("FROM Portfolio p "
                                + "LEFT JOIN FETCH p.valuePapers "
                                + "LEFT JOIN FETCH p.transactionEntries "
                                + "LEFT JOIN FETCH p.orders o "
                                + "LEFT JOIN FETCH o.valuePaper "
                                + "JOIN FETCH p.owner "
                                + "LEFT JOIN FETCH p.followers "
                                + "WHERE p.id = :id", Portfolio.class).setParameter("id", id).getSingleResult();
        } catch(NoResultException e) {
               return null;
        } catch(Exception e) {
               return null;
        }
		*/
}
	

}
