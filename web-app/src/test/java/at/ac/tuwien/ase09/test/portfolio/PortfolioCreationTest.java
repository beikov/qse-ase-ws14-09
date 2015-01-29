package at.ac.tuwien.ase09.test.portfolio;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import at.ac.tuwien.ase09.bean.PortfolioBean;
import at.ac.tuwien.ase09.bean.PortfolioCreationViewBean;
import at.ac.tuwien.ase09.bean.PortfolioViewBean;
import at.ac.tuwien.ase09.model.Money;
import at.ac.tuwien.ase09.model.Portfolio;
import at.ac.tuwien.ase09.test.AbstractSeleniumTest;
import at.ac.tuwien.ase09.test.DatabaseAware;
import at.ac.tuwien.ase09.test.portfolio.pageobject.PortfolioCreationForm;
import at.ac.tuwien.ase09.test.portfolio.pageobject.PortfolioCreationPage;
import at.ac.tuwien.ase09.test.portfolio.pageobject.PortfolioListPage;
import at.ac.tuwien.ase09.test.portfolio.pageobject.PortfolioVisibilitySettings;

@DatabaseAware
public class PortfolioCreationTest extends AbstractSeleniumTest {
	private static final long serialVersionUID = 1L;

	@Deployment(testable=false)
	public static Archive<?> createDeployment(){
		final String webapp_src = "src/main/webapp";
		Archive<?> archive = createSeleniumTestBaseDeployment()
			.addAsWebResource(new File(webapp_src + "/portfolio/_analystOpinions.xhtml"), "/portfolio/_analystOpinions.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_charts.xhtml"), "/portfolio/_charts.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_hidden.xhtml"), "/portfolio/_hidden.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_news.xhtml"), "/portfolio/_news.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_orderTable.xhtml"), "/portfolio/_orderTable.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_portfolio.xhtml"), "/portfolio/_portfolio.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_stats.xhtml"), "/portfolio/_stats.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_transactionTable.xhtml"), "/portfolio/_transactionTable.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/_valuePaperTable.xhtml"), "/portfolio/_valuePaperTable.xhtml")
			.addAsWebResource(new File(webapp_src + "/portfolio/view.xhtml"), "/portfolio/view.xhtml")
			
			.addAsWebResource(new File(webapp_src + "/protected/portfolio/create.xhtml"), "/protected/portfolio/create.xhtml")
			
			.addAsWebResource(new File(webapp_src + "/resources/portfolioVisibility/portfolioVisibility.xhtml"), "/resources/portfolioVisibility/portfolioVisibility.xhtml")
			.addClass(PortfolioBean.class)
			.addClass(PortfolioViewBean.class)
			.addClass(PortfolioCreationViewBean.class);
	
		System.out.println(archive.toString(true));
		return archive;
	}
	
	@Test
    public void testOpeningHomePage_new() {
		// Given
		final Portfolio newPortfolio = new Portfolio();
		newPortfolio.setName("NewPortfolio");
		newPortfolio.getSetting().setOrderFee(new Money(new BigDecimal("12.3"), null));
		newPortfolio.getVisibility().setPublicVisible(true);
		newPortfolio.getVisibility().setOrderHistoryVisible(true);
		
		// When
		final PortfolioCreationPage portfolioCreationPage = Graphene.goTo(PortfolioCreationPage.class);
		fillInPortfolioCreationForm(portfolioCreationPage.getCreationForm(), newPortfolio);
		
		// Then
		final PortfolioListPage portfolioListPage = Graphene.goTo(PortfolioListPage.class);
		assertTrue(portfolioListPage.getPortfolioTable().contains(newPortfolio.getName()));
	}
	
	private void fillInPortfolioCreationForm(PortfolioCreationForm form, Portfolio template){
		if(template.getName() != null){
			form.getNameTextBox().setText(template.getName());
		}
		if(template.getSetting() != null){
			if(template.getSetting().getCapitalReturnTax() != null){
				form.getCapitalReturnTaxTextBox().setText(template.getSetting().getCapitalReturnTax().toString());
			}
			if(template.getSetting().getOrderFee() != null){
				form.getCapitalReturnTaxTextBox().setText(template.getSetting().getOrderFee().getValue().toString());
			}
			if(template.getSetting().getPortfolioFee() != null){
				form.getCapitalReturnTaxTextBox().setText(template.getSetting().getPortfolioFee().getValue().toString());
			}
			if(template.getSetting().getStartCapital() != null){
				form.getCapitalReturnTaxTextBox().setText(template.getSetting().getStartCapital().getValue().toString());
			}
		}
		if(template.getVisibility() != null){
			PortfolioVisibilitySettings visibilitySettings = form.getVisibilitySettings();
			if(template.getVisibility().getPublicVisible() != null){
				visibilitySettings.getPublicSwitch().setState(template.getVisibility().getPublicVisible());
			}
			if(template.getVisibility().getAnalystOpinionsVisible() != null){
				visibilitySettings.getOpinionSwitch().setState(template.getVisibility().getAnalystOpinionsVisible());
			}
			if(template.getVisibility().getChartsVisible() != null){
				visibilitySettings.getChartsSwitch().setState(template.getVisibility().getChartsVisible());
			}
			if(template.getVisibility().getNewsVisible() != null){
				visibilitySettings.getNewsSwitch().setState(template.getVisibility().getNewsVisible());
			}
			if(template.getVisibility().getOrderHistoryVisible() != null){
				visibilitySettings.getOrderHistorySwitch().setState(template.getVisibility().getOrderHistoryVisible());
			}
			if(template.getVisibility().getStatisticsVisible() != null){
				visibilitySettings.getStatisticsSwitch().setState(template.getVisibility().getStatisticsVisible());
			}
			if(template.getVisibility().getTransactionHistoryVisible() != null){
				visibilitySettings.getTransactionHistorySwitch().setState(template.getVisibility().getTransactionHistoryVisible());
			}
			if(template.getVisibility().getValuePaperListVisible() != null){
				visibilitySettings.getValuePaperSwitch().setState(template.getVisibility().getValuePaperListVisible());
			}
		}
		form.save();
	}
}
