/**
 * 
 */
package at.ac.tuwien.ase09.test.pageobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.fragment.Root;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;

import com.google.common.base.Predicate;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 23.09.2014
 */
public abstract class AbstractPageElement implements PageElement {
    @Root
    protected WebElement rootElement;
    
    /* 
     * Qualifier actually limits reusability of the page fragment but is currently required.
     * See discussion: https://developer.jboss.org/message/911310#911310
     */
//    @Browser1
    @Drone
    protected WebDriver driver;
    
    @Override
    public ElementLocator getLocator(){
        throw new UnsupportedOperationException("Not a proxy");
    }
    
    protected void waitFor(By by) {
        waitFor(by, 100, 10);
    }

    protected void waitFor(By by, long poll, long timeout) {
        FluentWait<By> fluentWait = new FluentWait<By>(by);
        fluentWait.pollingEvery(poll, TimeUnit.MILLISECONDS);
        fluentWait.withTimeout(timeout, TimeUnit.SECONDS);
        fluentWait.until(new Predicate<By>() {
            public boolean apply(By by) {
                try {
                    List<WebElement> results = rootElement.findElements(by);
                    return !results.isEmpty() && results.get(0).isDisplayed();
                } catch (NoSuchElementException ex) {
                    return false;
                }
            }
        });
    }
    
    protected void appendText(WebElement inputElement, String text){
        Actions actions = new Actions(driver);
        actions.moveToElement(inputElement);
        actions.click();
        actions.sendKeys(text);
        actions.perform();
    }
    
    protected void setText(WebElement inputElement, String text){
        inputElement.clear();
        appendText(inputElement, text);
    }
    
    protected boolean isCheckboxChecked(WebElement checkboxElement){
        return !checkboxElement.findElements(By.cssSelector("div.ui-state-active")).isEmpty();
    }
    
    protected void setCheckboxState(WebElement checkboxElement, boolean checked){
        if ((checked == true && !isCheckboxChecked(checkboxElement)) || (checked == false && isCheckboxChecked(checkboxElement))){
            WebElement trigger = checkboxElement.findElement(By.cssSelector("div.ui-chkbox-box"));
            Graphene.guardAjax(trigger).click();
        }
    }
    
    protected void selectOptionByLabel(WebElement menuElement, String text) {
        selectOptionByLabel(menuElement, text, false);
    }
    
    protected String getSelectedOptionText(WebElement menuElement){
        String menuItemsContainerId = getEscapedElementId(menuElement) + "_panel";
        List<WebElement> selectedElements = driver.findElements(By.cssSelector("#" + menuItemsContainerId + " tr.ui-state-highlight"));
        if(selectedElements.isEmpty()){
            return null;
        }else{
            return selectedElements.get(0).getAttribute("data-label");
        }
    }
    
    protected String getEscapedElementId(WebElement element){
        return element.getAttribute("id").replaceAll(":", "\\\\:");
    }
    
    protected List<String> getOptionsText(WebElement menuElement){
        String menuId = getEscapedElementId(menuElement);
        String menuItemsContainerId = menuId + "_panel";
        List<WebElement> options = driver.findElements(By.cssSelector("#" + menuItemsContainerId + " tr.ui-selectonemenu-item"));
        if(options.isEmpty()){
            options = driver.findElements(By.cssSelector("#" + menuItemsContainerId + " li.ui-selectonemenu-item"));
        }
        
        List<String> optionsText = new ArrayList<>();
        for(WebElement option : options){
            optionsText.add(option.getAttribute("data-label"));
        }
        return optionsText;
    }
    
    protected void selectOptionByLabel(WebElement menuElement, String text, boolean guardAjax) {
        String menuId = getEscapedElementId(menuElement);
        WebElement trigger = menuElement.findElement(By.cssSelector("div.ui-selectonemenu-trigger"));
        trigger.click();
        
        String menuItemsContainerId = menuId + "_panel";
        List<WebElement> options = driver.findElements(By.cssSelector("#" + menuItemsContainerId + " tr.ui-selectonemenu-item"));
        if(options.isEmpty()){
            options = driver.findElements(By.cssSelector("#" + menuItemsContainerId + " li.ui-selectonemenu-item"));
        }
        String selectedOptionText = getSelectedOptionText(menuElement);
        for(WebElement option : options){
            if(option.getAttribute("data-label").equals(text)){
                if(guardAjax && !selectedOptionText.equals(text)){
                    Graphene.guardAjax(option).click();
                }else{
                    option.click();
                }
                break;
            }
        }
    }
    
    protected void pickPicklistItemByValue(WebElement menuElement, String value) {
        doubleClickPicklistItemByValue(menuElement.findElement(By.cssSelector("td:nth-child(1)")), value);
    }
    
    protected void unpickPicklistItemByValue(WebElement menuElement, String value) {
        doubleClickPicklistItemByValue(menuElement.findElement(By.cssSelector("td:nth-child(3)")), value);
    }
    
    private void doubleClickPicklistItemByValue(WebElement pickList, String value){
        List<WebElement> options = pickList.findElements(By.cssSelector("ul.ui-picklist-list li"));
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getText().equals(value)) {
                new Actions(driver).doubleClick(options.get(i)).perform();
                break;
            }
        }
    }
    
    protected List<String> getListBoxItems(WebElement listBox){
        List<String> selectItemLabels = new ArrayList<>();
        List<WebElement> selectItems = listBox.findElements(By.className("ui-selectlistbox-item"));
        for(WebElement selectItem : selectItems){
            selectItemLabels.add(selectItem.getText());
        }
        return selectItemLabels;
    }
    
    protected String getRootId(){
        return rootElement.getAttribute("id");
    }
    
    /**
     * Returns false in case of NoSuchElementException
     * @param webElement
     * @return
     */
    protected boolean isDisplayedSafe(WebElement webElement){
        try{
            return webElement.isDisplayed();
        }catch(NoSuchElementException e){
            return false;
        }
    }
    
    protected boolean isButtonActive(WebElement webElement){
        return containsClass(webElement, "ui-state-active");
    }
    
    protected boolean isButtonEnabled(WebElement webElement){
        return !containsClass(webElement, "ui-state-disabled");
    }
    
    protected boolean containsClass(WebElement webElement, String cssClass){
        String cssClasses = webElement.getAttribute("class");
        if(cssClasses != null){
            return Arrays.asList(cssClasses.split(" ")).contains(cssClass);
        }else{
            return false;
        }
    }
}
