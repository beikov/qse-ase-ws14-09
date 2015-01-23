package at.ac.tuwien.ase09.impl.rest;

import java.util.List;

import javax.ejb.Stateless;

import at.ac.tuwien.ase09.rest.UserResource;
import at.ac.tuwien.ase09.rest.model.PortfolioDto;

@Stateless
public class UserResourceImpl extends AbstractResource implements UserResource {
	
	@Override
	public List<PortfolioDto> getPortfolios(Long userId) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
}
