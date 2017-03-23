package org.redsys.testapp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.redsys.testapp.applogic.ApplicationManager;

import java.util.List;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class MainSMEVPage extends Page {

    public static final int INVALID_DATA_REQUEST_SERVICE_ID = 1;
    public static final int EXTRACTION_INVALID_DATA_REQUEST_SERVICE_ID = 2;
    public static final int DISABLED_DATA_REQUEST_SERVICE_ID = 3;

    public MainSMEVPage(WebDriver webDriver, ApplicationManager applicationManager) {
        super(webDriver, applicationManager);
    }

    private WebElement getNavTableCell(int i, int j) {
        return webDriver.findElement(By.xpath("/html/body/table/tbody/tr[" + (i + 1) + "]" + "/td[" + j + "]"));
    }

    public int getReqQueueSizeByServiceId (int serviceId) {
        return Integer.parseInt(getNavTableCell(serviceId, 3).getText());
    }

    public void waitExecuteAllReq (int serviceId) {
        while (getReqQueueSizeByServiceId(serviceId) != 0) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            webDriver.navigate().refresh();
        }
    }

    public void showLastMessages() {

    }
}
