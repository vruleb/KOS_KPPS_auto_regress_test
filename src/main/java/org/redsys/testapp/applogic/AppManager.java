package org.redsys.testapp.applogic;

import com.sun.istack.internal.NotNull;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.Zip;
import org.openqa.selenium.support.PageFactory;
import org.redsys.testapp.model.*;
import org.redsys.testapp.pages.SMEVSendingRequestPage;
import org.redsys.testapp.util.MD5Checksum;
import org.redsys.testapp.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */
public class ApplicationManager {

    public static final int GET_REQUEST = 1;
    public static final int ACT = 2;
    public static final int INTERNAL = 3;
    public static final int SEND_RESPONSE = 4;
    public static final int ADDITIONAL_INTERNAL = 10;

    private WebDriver webDriver;
    private Properties property;

    private SMEVSendingRequestPage smevSendingRequestPage;
    private FTPManager ftpManager;
    private KPPSDatabaseManager kppsDatabaseManager;

    public ApplicationManager(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.ftpManager = new FTPManager();
        this.kppsDatabaseManager = new KPPSDatabaseManager();
        this.property = new Properties();

        try {
            FileInputStream fis = new FileInputStream("./resources/config.properties");
            property.load(fis);
            connectToFileStorage();
            connectToKPPSDB();
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }

        this.smevSendingRequestPage = new SMEVSendingRequestPage(webDriver, this);
        PageFactory.initElements(webDriver, smevSendingRequestPage);
    }

    private void connectToFileStorage() throws IOException {
        InetAddress fileStorageHost = InetAddress.getByName(property.getProperty("file_storage_host"));
        int fileStoragePort = Integer.parseInt(property.getProperty("file_storage_port"));
        String fileStorageUser = property.getProperty("file_storage_user");
        String fileStoragePass = property.getProperty("file_storage_password");

        ftpManager.connect(fileStorageHost, fileStoragePort, fileStorageUser, fileStoragePass);
    }


    private void connectToKPPSDB() throws IOException, SQLException {
        String dbHost = property.getProperty("DB_KPPS_host");
        int dbPort = Integer.parseInt(property.getProperty("DB_KPPS_port"));
        String dbUser = property.getProperty("DB_KPPS_user");
        String dbPass = property.getProperty("DB_KPPS_password");

        kppsDatabaseManager.connect(dbHost, dbPort, dbUser, dbPass);
    }

    public void close() {
        try {
            ftpManager.closeConnection();
            kppsDatabaseManager.closeConnection();
            webDriver.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ZipPackage packageCreateHelper(boolean isUploaded,
                                          @NotNull File file,
                                          @NotNull String docTypeCode,
                                          @NotNull String docTypeVersion,
                                          UUID attachmentUUID,
                                          UUID packageUUID,
                                          Integer regionCode,
                                          String hash) {

        if (!isUploaded) {
            if (!file.isAbsolute() || !file.isFile()) {
                System.out.println("Не указан путь к файлу.");
                return null;
            }
            if (!file.exists()) {
                System.out.println("Файл не найден.");
                return null;
            }
            if (attachmentUUID == null) {
                attachmentUUID = UUID.randomUUID();
                System.out.println("AttachmentUUID не задан, был сгенерирован новый: " + attachmentUUID);
            }

        } else {
            if (attachmentUUID == null) {
                System.out.println("Для выгруженных в хранилище файлов необходимо указывать attachmentUUID.");
                return null;
            }
        }
        if (packageUUID == null) {
            packageUUID = UUID.randomUUID();
            System.out.println("PackageUUID не задан, был сгенерирован новый: " + packageUUID);
        }

        if (hash == null) {
            try {
                if (!isUploaded) {
                    hash = MD5Checksum.getMD5Checksum(file);
                } else {
                    File tmpFile = File.createTempFile("tmpPackage", ".zip");
                    ftpManager.downloadFile(file.getName(), attachmentUUID, tmpFile);
                    hash = MD5Checksum.getMD5Checksum(tmpFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        ZipPackage zipPackage = new ZipPackage(file, docTypeCode, docTypeVersion, regionCode,
                null, packageUUID, attachmentUUID, hash);

        if (!isUploaded) {
            ftpManager.uploadPackage(zipPackage);
        }

        return zipPackage;
    }

    public void getRequestResponseAdd(ZipPackage zipPackage) {
        DisabledDataRequest disabledDataRequest = new DisabledDataRequestAdd(zipPackage);
        webDriver.get("http://10.0.2.99:9080/fri.smev3.stub/send/getrqrsp/3");
        smevSendingRequestPage.setRequest(disabledDataRequest.getRequestStr());
        smevSendingRequestPage.sendRequest();
    }

    public void getRequestResponseUpdate(ZipPackage zipPackage) {
        DisabledDataRequest disabledDataRequest = new DisabledDataRequestUpdate(zipPackage);
        webDriver.get("http://10.0.2.99:9080/fri.smev3.stub/send/getrqrsp/3");
        smevSendingRequestPage.setRequest(disabledDataRequest.getRequestStr());
        smevSendingRequestPage.sendRequest();
    }

    public void getRequestResponseDelete(@NotNull UUID attachmentUUID) {
        DisabledDataRequest disabledDataRequest = new DisabledDataRequestDelete(attachmentUUID);
        webDriver.get("http://10.0.2.99:9080/fri.smev3.stub/send/getrqrsp/3");
        smevSendingRequestPage.setRequest(disabledDataRequest.getRequestStr());
        smevSendingRequestPage.sendRequest();
    }

    public void ExtractionInvalidDataRequest(String snils, Date validOn, Date dateFrom, Date dateTo) {
        ExtractionInvalidDataRequest extractReq = new ExtractionInvalidDataRequest(snils, validOn, dateFrom, dateTo);
        webDriver.get("http://10.0.2.99:9080/fri.smev3.stub/send/getrqrsp/2");
        smevSendingRequestPage.setRequest(extractReq.getRequestStr());
        smevSendingRequestPage.sendRequest();
    }

    public void findDocByAttribute(List<Pair> varValue, int range, int operTypeId, int reqResp) {
        List<String> procIDs = kppsDatabaseManager.getLastMessageUUID(range);
        Pair msgID_msg;
        for(String procID: procIDs) {
            msgID_msg = kppsDatabaseManager.getLogByOperTypeId(procID, operTypeId, reqResp);
        }
    }

    public void checkDocByAttribute(String xmlMsg, List<Pair> varValue) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlMsg);
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName(doc.getDocumentElement().getNodeName());
        for(Pair vv: varValue) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xpath.compile("//" + vv.getKey() + "[text()='" + vv.getValue() + "']");
            Node node = (Node)expr.evaluate(doc, XPathConstants.NODE);
            System.out.println(node.getBaseURI());
        }
    }

}
