package org.redsys.testapp.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.redsys.testapp.applogic.ApplicationManager;

import java.util.List;
import java.util.Set;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class InMessagesKPSSPage extends MainKPPSPage {

    @FindBy(id = "searchBt")
    private WebElement searchBtn;
    @FindBy(xpath = "/html/body/div[1]/div[2]/table/tbody/*")
    private List<WebElement> messageRows;

    public InMessagesKPSSPage(WebDriver webDriver, ApplicationManager applicationManager) {
        super(webDriver, applicationManager);
    }

    public void toLastMessages() {
        webDriver.findElement(By.id("radioToday")).click();
        searchBtn.click();
    }

    public int isRequiredMessage(String[] elements, String[] values) {
        return 1;
    }

    public int searchMessageByGetRequest(String[] elements, String[] values, WebDriver logMessageDriver) {
        if (elements.length != values.length) {
            return -1;
        }

        for (WebElement messageLogRef: messageRows) {
            //messageLogRef.findElement(By.tagName("a")).click();
            String url = messageLogRef.findElement(By.tagName("a")).getAttribute("href");
            logMessageDriver.get(url);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //webDriver.navigate().refresh();
        }
        return 1;
    }
}
