package org.redsys.testapp.applogic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.redsys.testapp.model.BaseRequest;
import org.redsys.testapp.model.KOSPackage;
import org.redsys.testapp.pages.SMEVSendingRequestPage;
import org.redsys.testapp.util.KPPSConstants;
import org.redsys.testapp.util.MD5Checksum;
import org.redsys.testapp.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 22.03.2017.
 */

public class AppManager {

    private WebDriver webDriver;
    private Properties property;

    private SMEVSendingRequestPage smevSendingRequestPage;
    private FTPManager ftpManager;
    private KPPSDatabaseManager kppsDatabaseManager;

    private String smevEmulatorURL;
    private String dataDir;

    public AppManager(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.ftpManager = new FTPManager();
        this.kppsDatabaseManager = new KPPSDatabaseManager();
        this.property = new Properties();

        try {
            FileInputStream fis = new FileInputStream("./config.properties");
            property.load(fis);
            smevEmulatorURL = property.getProperty("smev_emulator_url");
            dataDir = property.getProperty("data_dir");

            connectToFileStorage();
            connectToKPPSDB();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

    public KOSPackage packageCreateHelper(boolean isUploaded,
                                          File file,
                                          String docTypeCode,
                                          String docTypeVersion,
                                          UUID attachmentUUID,
                                          UUID packageUUID,
                                          Integer regionCode,
                                          String hash,
                                          String sourceId) {

        if (!isUploaded) {
            if (!file.isFile()) {
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
        //попробовать вычислить хеш
        if (hash == null) {
            if (!isUploaded) {
                hash = MD5Checksum.getMD5Checksum(file);
            } else {
                hash = ftpManager.getRemoteFileHash(file.getName(), attachmentUUID);
            }
            //если не удалось - выход
            if (hash == null) {
                return null;
            }
        }

        KOSPackage KOSPackage = new KOSPackage(file, docTypeCode, docTypeVersion, regionCode,
                sourceId, packageUUID.toString(), attachmentUUID.toString(), hash);

        if (!isUploaded) {
            if (!ftpManager.uploadPackage(KOSPackage)) {
                System.out.println("Не удалось выгрузить файл.");
                return null;
            }
        }

        return KOSPackage;
    }

    public void sendResponse(int serviceId, BaseRequest request) {
        try {
            webDriver.get(smevEmulatorURL + "send/getrqrsp/" + serviceId);
            smevSendingRequestPage.setRequest(request.getRequestStr());
            smevSendingRequestPage.sendRequest();
            System.out.println("Запрос отправлен.");
        } catch (Exception e) {
            System.out.println("Не удалось отправить запрос.");
        }
    }

    public String getDocUUID(List<Pair> varValue, int range, int operTypeId, int reqResp) {
        List<String> procUUIDs = kppsDatabaseManager.getLastMessageUUID(range);
        String logUUID, log;
        boolean res;
        for (String procUUID : procUUIDs) {
            System.out.println(procUUID);
            if (procUUID == null) {
                continue;
            }
            logUUID = kppsDatabaseManager.getLogUUID(procUUID, operTypeId);
            log = kppsDatabaseManager.getLog(logUUID, reqResp);
            try {
                if (checkDoc(log, varValue)) {
                    return procUUID;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean checkDoc(String xmlMsg, List<Pair> varValue) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(xmlMsg.getBytes("utf-8"))));

        Element root = doc.getDocumentElement();
        root.normalize();

        for (Pair vv : varValue) {
            NodeList cell = root.getElementsByTagName(vv.getKey());
            boolean found = false;
            Element elem;
            for (int i = 0; i < cell.getLength(); i++) {
                if (cell.item(i).getTextContent().contains(vv.getValue())) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }

    public boolean isDocProcessed(String procUUID) {
        int statusId = kppsDatabaseManager.getProcStatus(procUUID);
        if (statusId == KPPSConstants.STATUS_PROCESSING) {
            return true;
        } else {
            return false;
        }
    }

    public boolean waitDocProcessed(String procUUID, int timeout, int attemps) {
        for (int i = 0; i < attemps; i++) {
            if (isDocProcessed(procUUID)) {
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public File getPackage(String packageName) {
        return new File(dataDir + "/" + packageName);
    }

    public FTPManager getFtpManager() {
        return ftpManager;
    }

    public KPPSDatabaseManager getKppsDatabaseManager() {
        return kppsDatabaseManager;
    }
}
