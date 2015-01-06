package at.ac.tuwien.ase09.test.pageobject;

import org.openqa.selenium.By;

public class InputSwitch extends AbstractPageElement {
	
	public void setState(boolean state){
		if(getState() != state){
			rootElement.click();
		}
	}
	
	public boolean getState(){
		return rootElement.findElement(By.id(getRootId() + "_input")).getAttribute("checked") != null;
	}
	
	@Override
	public boolean isDisplayed() {
		return rootElement.isDisplayed();
	}
	
}
