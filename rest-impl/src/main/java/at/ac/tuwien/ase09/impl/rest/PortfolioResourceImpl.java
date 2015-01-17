package at.ac.tuwien.ase09.impl.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.ase09.data.ValuePaperDataAccess;
import at.ac.tuwien.ase09.model.PortfolioValuePaper;
import at.ac.tuwien.ase09.rest.PortfolioResource;
import at.ac.tuwien.ase09.rest.model.PortfolioValuePaperDto;

@Stateless
public class PortfolioResourceImpl extends AbstractResource implements PortfolioResource {

	@Inject
	private ValuePaperDataAccess valuePaperDataAcess;
	@Override
	public List<PortfolioValuePaperDto> getValuePapers(long portfolioId) {
		List<PortfolioValuePaper> valuePapers = valuePaperDataAcess.getValuePapersForPortfolio(portfolioId);
		List<PortfolioValuePaperDto> results = new ArrayList<>();
//		for(ValuePaper vp : valuePapers){
//			results.add(createFromEntity(vp));
//		}
		
		//TODO: correct
		
		return results;
	}
	
	
}
