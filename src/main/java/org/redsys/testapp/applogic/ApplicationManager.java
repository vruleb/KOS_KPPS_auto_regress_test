package org.redsys.testapp.applogic;

import org.openqa.selenium.WebDriver;
import org.redsys.testapp.pages.AuthorizationKPPSPage;
import org.redsys.testapp.pages.InMessagesKPSSPage;
import org.redsys.testapp.pages.SMEVSendingRequestPage;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class ApplicationManager {

    private WebDriver webDriver;

    private SMEVSendingRequestPage smevSendingRequestPage;
    private AuthorizationKPPSPage authorizationKPPSPage;
    private InMessagesKPSSPage inMessagesKPSSPage;

    public ApplicationManager(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
