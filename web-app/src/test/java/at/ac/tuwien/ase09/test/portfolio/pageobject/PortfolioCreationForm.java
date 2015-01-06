package at.ac.tuwien.ase09.test.portfolio.pageobject;

import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.WebElement;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;
import at.ac.tuwien.ase09.test.pageabstraction.NamingContainer;
import at.ac.tuwien.ase09.test.pageobject.TextBox;

public class PortfolioCreationForm {
	@FindBy(relativeId="name")
	private TextBox nameTextBox;
	@FindBy(relativeId="startCapital")
	private TextBox startCapitalTextBox;
	@FindBy(relativeId="orderFee")
	private TextBox orderFeeTextBox;
	@FindBy(relativeId="portfolioFee")
	private TextBox portfolioFeeTextBox;
	@FindBy(relativeId="capitalReturnTax")
	private TextBox capitalReturnTaxTextBox;
	@NamingContainer("portfolioVisibility")
	@FindBy(relativeId="portfolioVisibility:visibilitySettings")
	private PortfolioVisibilitySettings visibilitySettings;
	@FindBy(relativeId="save")
	private WebElement saveButton;
	@FindBy(relativeId="reset")
	private WebElement resetButton;
	
	public PortfolioVisibilitySettings getVisibilitySettings() {
		return visibilitySettings;
	}

	public TextBox getNameTextBox() {
		return nameTextBox;
	}

	public TextBox getStartCapitalTextBox() {
		return startCapitalTextBox;
	}

	public TextBox getOrderFeeTextBox() {
		return orderFeeTextBox;
	}

	public TextBox getPortfolioFeeTextBox() {
		return portfolioFeeTextBox;
	}

	public TextBox getCapitalReturnTaxTextBox() {
		return capitalReturnTaxTextBox;
	}
	
	public void save(){
		Graphene.guardAjax(saveButton).click();
	}
	
	public void reset(){
		resetButton.click();
	}
	
}
