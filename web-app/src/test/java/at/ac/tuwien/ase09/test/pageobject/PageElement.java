/**
 * 
 */
package at.ac.tuwien.ase09.test.pageobject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 23.09.2014
 */
public interface PageElement {
    public ElementLocator getLocator();
    public boolean isDisplayed();
}
