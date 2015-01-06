/**
 * 
 */
package at.ac.tuwien.ase09.test.pageobject;

import org.openqa.selenium.WebElement;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 17.12.2014
 */
public class TextBox extends AbstractPageElement {
    
    public String getText(){
        return rootElement.getAttribute("value");
    }
    
    public String getColor(){
        return rootElement.getCssValue("color");
    }

    public void setText(String text){
        setText(rootElement, text);
    }
    
    public WebElement getInputElement(){
        return rootElement;
    }
    
    /* (non-Javadoc)
     * @see at.ac.tuwien.ase09.test.pageobject.PageElement#isDisplayed()
     */
    @Override
    public boolean isDisplayed() {
        return rootElement.isDisplayed();
    }

}
