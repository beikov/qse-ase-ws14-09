/**
 * 
 */
package at.ac.tuwien.ase09.test.pageabstraction;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 07.10.2014
 */
public class NamingContainerAwareWebElement extends RemoteWebElement {

    private final String namingContainer;
    private final RemoteWebElement webElem;
    
    /**
     * @param wrappedSearchContext
     * @param id
     */
    public NamingContainerAwareWebElement(WebElement webElem, String namingContainer) {
        this.webElem = (RemoteWebElement) webElem;
        this.namingContainer = namingContainer;
    }
    
    public void setParent(RemoteWebDriver parent) {
        webElem.setParent(parent);
    }



    public String getId() {
        return webElem.getId();
    }



    public void setId(String id) {
        webElem.setId(id);
    }



    public void setFileDetector(FileDetector detector) {
        webElem.setFileDetector(detector);
    }



    public void click() {
        webElem.click();
    }



    public void submit() {
        webElem.submit();
    }



    public void sendKeys(CharSequence... keysToSend) {
        webElem.sendKeys(keysToSend);
    }



    public void clear() {
        webElem.clear();
    }



    public String getTagName() {
        return webElem.getTagName();
    }



    public String getAttribute(String name) {
        return webElem.getAttribute(name);
    }



    public boolean isSelected() {
        return webElem.isSelected();
    }



    public boolean isEnabled() {
        return webElem.isEnabled();
    }



    public String getText() {
        return webElem.getText();
    }



    public String getCssValue(String propertyName) {
        return webElem.getCssValue(propertyName);
    }



    public List<WebElement> findElements(By by) {
        return webElem.findElements(by);
    }



    public WebElement findElement(By by) {
        return webElem.findElement(by);
    }



    public WebElement findElementById(String using) {
        return webElem.findElementById(using);
    }



    public List<WebElement> findElementsById(String using) {
        return webElem.findElementsById(using);
    }



    public WebElement findElementByLinkText(String using) {
        return webElem.findElementByLinkText(using);
    }



    public List<WebElement> findElementsByLinkText(String using) {
        return webElem.findElementsByLinkText(using);
    }



    public WebElement findElementByName(String using) {
        return webElem.findElementByName(using);
    }



    public List<WebElement> findElementsByName(String using) {
        return webElem.findElementsByName(using);
    }



    public WebElement findElementByClassName(String using) {
        return webElem.findElementByClassName(using);
    }



    public List<WebElement> findElementsByClassName(String using) {
        return webElem.findElementsByClassName(using);
    }



    public WebElement findElementByCssSelector(String using) {
        return webElem.findElementByCssSelector(using);
    }



    public List<WebElement> findElementsByCssSelector(String using) {
        return webElem.findElementsByCssSelector(using);
    }



    public WebElement findElementByXPath(String using) {
        return webElem.findElementByXPath(using);
    }



    public List<WebElement> findElementsByXPath(String using) {
        return webElem.findElementsByXPath(using);
    }



    public WebElement findElementByPartialLinkText(String using) {
        return webElem.findElementByPartialLinkText(using);
    }



    public List<WebElement> findElementsByPartialLinkText(String using) {
        return webElem.findElementsByPartialLinkText(using);
    }



    public WebElement findElementByTagName(String using) {
        return webElem.findElementByTagName(using);
    }



    public List<WebElement> findElementsByTagName(String using) {
        return webElem.findElementsByTagName(using);
    }



    public boolean equals(Object obj) {
        return webElem.equals(obj);
    }



    public int hashCode() {
        return webElem.hashCode();
    }



    public WebDriver getWrappedDriver() {
        return webElem.getWrappedDriver();
    }



    public boolean isDisplayed() {
        return webElem.isDisplayed();
    }



    public Point getLocation() {
        return webElem.getLocation();
    }



    public Dimension getSize() {
        return webElem.getSize();
    }



    public Coordinates getCoordinates() {
        return webElem.getCoordinates();
    }



    public String toString() {
        return webElem.toString();
    }



    public String getNamingContainer(){
        return namingContainer;
    }
    
}
