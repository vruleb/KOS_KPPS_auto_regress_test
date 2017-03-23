package org.redsys.testapp.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.redsys.testapp.applogic.ApplicationManager;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class AuthorizationKPPSPage extends Page {

    //страница авторизации
    @FindBy(name = "j_username")
    private WebElement usernameField;
    @FindBy(name = "j_password")
    private WebElement passwordField;
    @FindBy(xpath = "/html/body/form/input")
    private WebElement btnLogin;

    public AuthorizationKPPSPage(WebDriver webDriver, ApplicationManager applicationManager) {
        super(webDriver, applicationManager);
    }

    public MainKPPSPage login(String user, String pass) {
        usernameField.sendKeys(user);
        passwordField.sendKeys(pass);
        btnLogin.click();
        MainKPPSPage mainKPPSPage = new MainKPPSPage(webDriver, applicationManager);
        PageFactory.initElements(webDriver, mainKPPSPage);
        return mainKPPSPage;
    }
}
