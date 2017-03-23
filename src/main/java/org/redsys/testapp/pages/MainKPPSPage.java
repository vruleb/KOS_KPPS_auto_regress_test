package org.redsys.testapp.pages;

import jdk.internal.util.xml.impl.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.redsys.testapp.applogic.ApplicationManager;

import java.util.List;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class MainKPPSPage extends Page {

    @FindBy(xpath = "/html/body/table[1]/tbody/tr/td[2]/a")
    private WebElement btnLogout;
    @FindBy(xpath = "/html/body/table[2]/tbody/tr/td/div/div/div/div/ul/li[1]/a")
    private WebElement inMessageJournal;

    public MainKPPSPage(WebDriver webDriver, ApplicationManager applicationManager) {
        super(webDriver, applicationManager);
    }

    public AuthorizationKPPSPage logout() {
        btnLogout.click();
        return PageFactory.initElements(webDriver, AuthorizationKPPSPage.class);
    }

    public InMessagesKPSSPage toInMessages() {
        inMessageJournal.click();
        InMessagesKPSSPage inMessagesKPSSPage = new InMessagesKPSSPage(webDriver, applicationManager);
        PageFactory.initElements(webDriver, inMessagesKPSSPage);
        return  inMessagesKPSSPage;
    }
}
