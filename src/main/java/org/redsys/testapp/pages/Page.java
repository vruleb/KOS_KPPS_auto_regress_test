package org.redsys.testapp.pages;

import org.openqa.selenium.WebDriver;
import org.redsys.testapp.applogic.ApplicationManager;

/**
 * Created by vyacheslav.vrubel on 20.03.2017.
 */
public abstract class Page {

    protected WebDriver webDriver;
    protected ApplicationManager applicationManager;

    public Page(WebDriver webDriver, ApplicationManager applicationManager)
    {
        this.webDriver = webDriver;
        this.applicationManager = applicationManager;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public String getTitle() {
        return webDriver.getTitle();
    }
}
