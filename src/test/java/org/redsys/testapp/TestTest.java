package org.redsys.testapp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.redsys.testapp.applogic.AppManager;
import org.redsys.testapp.applogic.KPPSDatabaseManager;
import org.redsys.testapp.util.KPPSConstants;
import org.redsys.testapp.util.Pair;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class TestTest {

    private AppManager appManager;

    @Before
    public void init() {
        appManager = new AppManager(new ChromeDriver());
    }

    @Test
    public void findDoc() {
        List<Pair> varValue = new ArrayList<>();
        varValue.add(new Pair("ns2:MessageID", "88f8ce74-07e0-41dd-8f0e-b17077879afb"));
        String msg = appManager.getKppsDatabaseManager().getLog("9ce2fa4f-45e9-4a2e-b8c3-eb7d0989d41e", 1);
        System.out.println(msg);
        try {
            if (appManager.checkDoc(msg, varValue)) {
                System.out.println("Найден!");
            } else {
                System.out.println("Не найден!");
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findMessage(){
        List<Pair> varValue = new ArrayList<>();
        varValue.add(new Pair("ns2:MessageID", "1cb1da9c-4190-40d5-8e06-37c94229ecbb"));
        varValue.add(new Pair("ns2:MessageType", "REQUEST"));

        String res = appManager.getDocUUID(varValue, 7, KPPSConstants.GET_REQUEST, 1);
        System.out.println("Результат: " + res);
    }

    @After
    public void close() {
        appManager.close();
    }
}
