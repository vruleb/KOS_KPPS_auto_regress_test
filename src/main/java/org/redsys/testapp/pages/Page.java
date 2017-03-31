package org.redsys.testapp.pages;

import org.openqa.selenium.WebDriver;
import org.redsys.testapp.applogic.AppManager;

/**
 * Created by vyacheslav.vrubel on 20.03.2017.
 */
public abstract class Page {

    protected WebDriver webDriver;
    protected AppManager appManager;

    public Page(WebDriver webDriver, AppManager appManager) {
        this.webDriver = webDriver;
        this.appManager = appManager;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public String getTitle() {
        return webDriver.getTitle();
    }
}
