/**
 * 
 */
package at.ac.tuwien.ase09.test.pageobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.proxy.GrapheneProxyInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;

/**
 * @author Moritz Becker <m.becker@curecomp.com>
 * @company curecomp
 * @date 29.10.2014
 */
public abstract class AbstractTable extends AbstractPageElement {
    @FindBy(xpath = "descendant::div[@id='{rootId}:dataTable_paginator_top']/span[@class='ui-paginator-current']")
    private WebElement pageStatus;
    @FindBy(xpath="descendant::div[@id='{rootId}:dataTable_paginator_top']/select")
    private WebElement pageSizeDropDown;
    @FindBy(
            xpath = "descendant::tbody/tr")
    private List<WebElement> tableRows;
    

    @Override
    public boolean isDisplayed() {
        try{
            if(tableRows.size() > 0){
                for(WebElement tableRow : tableRows){
                    if(!tableRow.isDisplayed()){
                        return false;
                    }
                }
                return true;
            }
        }catch(NoSuchElementException e){
            // ignore
        }
        
        return false;
    }
    
    public int getPageCount(){
        return Integer.valueOf(pageStatus.getText().split("/")[1]);
    }
    
    public int getCurrentPage(){
        return Integer.valueOf(pageStatus.getText().split("/")[0]);
    }
    
    public int[] getAvailablePageSizes(){
        Select select = new Select(pageSizeDropDown);
        List<WebElement> options = select.getOptions();
        int[] result = new int[options.size()];
        for(int i = 0; i < options.size(); i++){
            result[i] = Integer.valueOf(options.get(i).getText());
        }
        
        return result;
    }
    
    public void setPageSize(int pageSize){
        Select select = new Select(pageSizeDropDown);
        Graphene.guardAjax(select).selectByVisibleText(Integer.toString(pageSize));
    }
    
    public void selectByName(String name){
    	List<WebElement> row = getRowByName(name);
    	if(row != null){
    		Graphene.guardAjax(row.get(getNameColumnIndex())).click();
    	}
    }
    
    private List<WebElement> getRowByName(String name){
    	List<List<WebElement>> tableRows = getResolvedTableRows();
        for(List<WebElement> cells : tableRows){
            if(cells.get(getNameColumnIndex()).getText().equals(name)){
            	return cells;
            }
        }
        return null;
    }
    
    public boolean contains(String name){
    	return getRowByName(name) != null;
    }
    
    protected abstract int getNameColumnIndex();
    
    protected List<List<WebElement>> getResolvedTableRows(){
        List<WebElement> unwrappedRows = ((GrapheneProxyInstance) tableRows).unwrap();
        List<List<WebElement>> rows = new ArrayList<List<WebElement>>();
        for (WebElement row : unwrappedRows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            rows.add(cells);
        }
        
        if(rows.size() == 1 && (rows.get(0).size() == 1)){
            // the table is empty
            return Collections.EMPTY_LIST;
        }
        return rows;
    }
}
