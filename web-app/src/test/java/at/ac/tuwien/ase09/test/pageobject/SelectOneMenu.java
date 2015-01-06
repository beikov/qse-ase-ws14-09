/**
 * 
 */
package at.ac.tuwien.ase09.test.pageobject;

import java.util.List;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 30.12.2014
 */
public class SelectOneMenu extends AbstractPageElement {

    /* (non-Javadoc)
     * @see at.ac.tuwien.ase09.test.pageobject.PageElement#isDisplayed()
     */
    @Override
    public boolean isDisplayed() {
        return rootElement.isDisplayed();
    }
    
    public void setSelectedOptionByText(String optionText, boolean guardAjax){
        selectOptionByLabel(rootElement, optionText, guardAjax);
    }
    
    public String getSelectedOptionText(){
        return getSelectedOptionText(rootElement);
    }
    
    public List<String> getOptionsText(){
        return getOptionsText(rootElement);
    }
    

}
