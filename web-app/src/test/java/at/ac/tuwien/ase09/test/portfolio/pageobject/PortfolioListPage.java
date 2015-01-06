package at.ac.tuwien.ase09.test.portfolio.pageobject;

import org.jboss.arquillian.graphene.page.Location;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;

@Location("protected/portfolio/list.xhtml")
public class PortfolioListPage {
	@FindBy(id="portfolioTable")
	private PortfolioTable portfolioTable;

	public PortfolioTable getPortfolioTable() {
		return portfolioTable;
	}
	
}
