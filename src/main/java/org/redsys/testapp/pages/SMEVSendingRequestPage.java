package org.redsys.testapp.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.redsys.testapp.applogic.ApplicationManager;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class SMEVSendingRequestPage extends Page {

    @FindBy(id = "btnSend")
    private WebElement sendBtn;
    @FindBy(id = "request")
    private WebElement reqTextArea;

    public SMEVSendingRequestPage(WebDriver webDriver, ApplicationManager applicationManager)
    {
        super(webDriver, applicationManager);
    }

    public void setRequest(String requestStr) {
        //reqTextArea.sendKeys(requestStr);
        ((JavascriptExecutor)webDriver).executeScript("arguments[0].value = arguments[1];", reqTextArea,
                requestStr);
        reqTextArea.sendKeys(" ");
    }

    public void sendRequest() {
        sendBtn.click();
    }

}
