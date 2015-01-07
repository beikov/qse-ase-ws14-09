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
public class TextArea extends AbstractPageElement {
    @FindBy(relativeId="inputTextarea")
    private WebElement input;
    
    public String getText(){
        return input.getText();
    }
    
    public String getColor(){
        return input.getCssValue("color");
    }

    public void setText(String text){
        setText(input, text);
    }
    
    public WebElement getInputElement(){
        return input;
    }
    
    /* (non-Javadoc)
     * @see at.ac.tuwien.ase09.test.pageobject.PageElement#isDisplayed()
     */
    @Override
    public boolean isDisplayed() {
        return input.isDisplayed();
    }

}
