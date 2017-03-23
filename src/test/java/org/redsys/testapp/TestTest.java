package org.redsys.testapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.redsys.testapp.applogic.ApplicationManager;
import org.redsys.testapp.applogic.FTPManager;
import org.redsys.testapp.applogic.KPPSDatabaseManager;
import org.redsys.testapp.model.BaseRequest;
import org.redsys.testapp.model.DisabledDataRequestAdd;
import org.redsys.testapp.model.ZipPackage;
import org.redsys.testapp.pages.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class TestTest {

    private FTPManager ftpManager;
    private WebDriver webDriver;
    private ApplicationManager applicationManager;

    @Before
    public void init() {
        ftpManager = new FTPManager();
        webDriver = new ChromeDriver();
        applicationManager = new ApplicationManager(webDriver);
        try {
            ftpManager.connect(InetAddress.getByName("10.0.2.40"), 21, "user", "password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void AddTest1() {
        ZipPackage zipPackage;
        //ftpManager.uploadPackage(zipPackage);
    }

    @Test
    public void DeleteTest1() {
        ftpManager.deletePackage(UUID.fromString("d34521c8-a2a3-4ba5-9796-b3061d570a3f"),
                "MSE-ACT-20160101091011-003f52cc-7777-11e6-9d9d-cec0c932ce24.zip");
    }

    @Test
    public void DisabilityDataRequestAddTest() {

        /*
        ZipPackage zipPackage;
        try {
            BaseRequest disabledDataRequestAdd = new DisabledDataRequestAdd(zipPackage);
            System.out.println(disabledDataRequestAdd.getRequestStr());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void GetHashTest() {
        ZipPackage zipPackage;

        System.out.println(ftpManager.getRemoteFileHash("MSE-ACT-20160101091011-003f52cc-7777-11e6-9d9d-cec0c932ce24.zip", UUID.fromString("c8398ef0-6c69-4e29-b500-3e019fd7374c")));
    }

    @Test
    public void SendReqTest() {
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://10.0.2.99:9080/fri.smev3.stub/send/getrqrsp/3");
        SMEVSendingRequestPage sendingReqPage = new SMEVSendingRequestPage(webDriver, applicationManager);

        PageFactory.initElements(webDriver, sendingReqPage);

        ZipPackage zipPackage = new ZipPackage(new File("C:/Users/vyacheslav.vrubel/Desktop/MSE-ACT-20160101091011-003f52cc-7777-11e6-9d9d-cec0c932ce24.zip"),
                "ACT",
                "20160101",
                null,
                UUID.fromString("003f52cc-7777-11e6-9d9d-cec0c932ce24"),
                UUID.fromString("c8398ef0-6c69-4e29-b500-3e019fd7374c"),
                null
        );
        //ftpManager.updateFileHash(zipPackage);
        try {
            BaseRequest disabledDataRequestAdd = new DisabledDataRequestAdd(zipPackage);
            sendingReqPage.setRequest(disabledDataRequestAdd.getRequestStr());
            sendingReqPage.sendRequest();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        webDriver.close();
    }

    @Test
    public void SMEVValuesTest () {
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://10.0.2.99:9080/fri.smev3.stub/");

        MainSMEVPage mainSMEVPage = PageFactory.initElements(webDriver, MainSMEVPage.class);
        //System.out.println(mainSMEVPage.getReqQueueSizeByServiceId(MainSMEVPage.DISABLED_DATA_REQUEST_SERVICE_ID));
        //mainSMEVPage.getReqQueueSizeByServiceId(MainSMEVPage.EXTRACTION_INVALID_DATA_REQUEST_SERVICE_ID);
        //mainSMEVPage.getReqQueueSizeByServiceId(MainSMEVPage.INVALID_DATA_REQUEST_SERVICE_ID);


        SendReqTest();
        webDriver.navigate().refresh();
        mainSMEVPage.waitExecuteAllReq(MainSMEVPage.DISABLED_DATA_REQUEST_SERVICE_ID);
    }

    @Test
    public void intTest () {
        int a;
    }

    @Test
    public void loginTest() {
        webDriver.get("http://10.0.2.99:9080/fri.web/secure/main/");

        AuthorizationKPPSPage authorizationKPPSPage = PageFactory.initElements(webDriver, AuthorizationKPPSPage.class);
        MainKPPSPage mainKPPSPage = authorizationKPPSPage.login("dev_admin","1QAZ2wsx");
        mainKPPSPage.logout();
    }

    @Test
    public void navMessageTest() {
        webDriver.get("http://10.0.2.99:9080/fri.web/secure/main/");

        AuthorizationKPPSPage authorizationKPPSPage = new AuthorizationKPPSPage(webDriver, applicationManager);
        PageFactory.initElements(webDriver, authorizationKPPSPage);
        MainKPPSPage mainKPPSPage = authorizationKPPSPage.login("dev_admin","1QAZ2wsx");
        //mainKPPSPage.toLastMessages();

        InMessagesKPSSPage inMessagesKPSSPage = mainKPPSPage.toInMessages();
        inMessagesKPSSPage.toLastMessages();
        String [] elements = new String[1];
        String [] values = new String[1];

        WebDriver webDriver2 = new ChromeDriver();
        webDriver2.get("http://10.0.2.99:9080/fri.web/secure/main/");
        Set<Cookie> cookies=null;
        try
        {
            cookies=webDriver.manage().getCookies();
        }
        catch(Throwable e)
        {
            System.err.println("Error While getting Cookies: "+ e.getMessage());
        }

        try
        {
            for(Cookie cookie:cookies)
            {
                webDriver2.manage().addCookie(cookie);
            }
        }
        catch(Throwable e)
        {
            System.err.println("Error While setting Cookies: "+ e.getMessage());
        }

        inMessagesKPSSPage.searchMessageByGetRequest(elements, values, webDriver2);
    }

    @Test
    public void DBConnTest() {
        KPPSDatabaseManager kppsDatabaseManager = new KPPSDatabaseManager("10.0.2.99", 5432, "postgres", "postgres");
        kppsDatabaseManager.getLastMessageUUID(4);
        System.out.println(kppsDatabaseManager.getLogByOperTypeId("47a2f320-b3a7-4436-a90e-8be5d3de374f", 1));
    }

    @After
    public void close() {
        try {
            ftpManager.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
