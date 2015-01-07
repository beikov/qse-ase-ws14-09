package at.ac.tuwien.ase09.test.portfolio.pageobject;

import org.jboss.arquillian.graphene.page.Location;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;
import at.ac.tuwien.ase09.test.pageabstraction.NamingContainer;

@Location("protected/portfolio/create.xhtml?user=DEFAULT")
public class PortfolioCreationPage {
	@NamingContainer
	@FindBy(id="createForm")
	private PortfolioCreationForm creationForm;

	public PortfolioCreationForm getCreationForm() {
		return creationForm;
	}
	
}
