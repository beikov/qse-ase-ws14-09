package at.ac.tuwien.ase09.test.pageobject;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;

public abstract class TableTab<T extends AbstractTable> extends AbstractTab {
    @FindBy(relativeId="dataTable")
    protected T table;
    
    @Override
    public boolean isDisplayed() {
        return table.isDisplayed();
    }

    public T getTable() {
        return table;
    }
    
}
