package at.ac.tuwien.ase09.test.pageobject;

import org.jboss.arquillian.graphene.Graphene;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import at.ac.tuwien.ase09.test.pageabstraction.FindBy;
import com.google.common.base.Predicate;

public class FileUpload extends AbstractPageElement {
    @FindBy(relativeId="fileUpload_input")
    private WebElement filePathInput;
    @FindBy(css="button.ui-fileupload-upload")
    private WebElement uploadButton;
    @FindBy(css="button.ui-fileupload-cancel")
    private WebElement cancelButton;
    @FindBy(relativeId="fileDownload:fileDownload")
    private WebElement fileDownloadLink;
    
    @Override
    public boolean isDisplayed() {
        return uploadButton.isDisplayed();
    }
    
    public void uploadFile(String filePath){
        filePathInput.sendKeys(filePath);
        Graphene.waitModel(driver).until().element(uploadButton).is().enabled();
        Graphene.guardAjax(uploadButton).click();
        Graphene.waitModel(driver).until().element(cancelButton).is().not().enabled();
    }

}
