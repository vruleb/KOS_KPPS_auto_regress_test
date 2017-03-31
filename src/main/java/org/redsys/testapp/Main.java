package org.redsys.testapp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.redsys.testapp.applogic.AppManager;
import org.redsys.testapp.model.*;
import org.redsys.testapp.pages.MainSMEVPage;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 24.03.2017.
 */
public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            showHelp();
            return;
        }

        WebDriver webDriver = new ChromeDriver();
        AppManager appManager = new AppManager(webDriver);

        String mode = args[0];
        mode = mode.toLowerCase();
        switch (mode) {
            case "extract":
                extractMode(args, appManager);
                break;
            case "add":
                addUpdateMode(0, args, appManager);
                break;
            case "update":
                addUpdateMode(1, args, appManager);
                break;
            case "delete":
                deleteMode(args, appManager);
                break;
            default:
                System.out.println("Неверная операция.");
                break;
        }
        webDriver.quit();
    }

    private static void deleteMode(String[] args, AppManager appManager) {
        if (args.length < 2) {
            System.out.println("Недостаточно аргументов.");
            return;
        }
        UUID packageUUID = UUID.fromString(args[1]);
        try {
            appManager.sendResponse(MainSMEVPage.DISABLED_DATA_REQUEST, new DisabledDataRequestDelete(packageUUID.toString()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void extractMode(String[] args, AppManager appManager) {

        if (args.length < 4) {
            System.out.println("Недостаточно аргументов.");
            return;
        }

        String snils = args[1];
        snils = snils.replaceAll(" ", "");
        snils = snils.replaceAll("-", "");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date validOn, dateFrom, dateTo;
        try {
            validOn = df.parse(args[2]);
            dateFrom = df.parse(args[3]);
            dateTo = df.parse(args[4]);
        } catch (ParseException e) {
            System.out.println("Неверный формат аргумента. " + e.getMessage());
            return;
        }
        try {
            appManager.sendResponse(MainSMEVPage.EXTRACTION_INVALID_DATA_REQUEST,
                    new ExtractionInvalidDataRequest(snils, validOn, dateFrom, dateTo));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void addUpdateMode(int mode, String[] args, AppManager appManager) {

        Boolean isUploaded = null;
        File file = null;
        String docTypeCode = null;
        String docTypeVersion = null;
        String sourceId = null;

        UUID attachmentUUID = null;
        UUID packageUUID = null;
        Integer regionCode = null;
        String hash = null;
        // обработка обязательных аргументов
        try {
            isUploaded = Boolean.parseBoolean(args[1]);
            file = new File(args[2]);
            docTypeCode = args[3];
            docTypeVersion = args[4];

            for (int i = 5; i < args.length; i++) {
                switch (args[i]) {
                    case "-attachUUID":
                        attachmentUUID = UUID.fromString(args[i + 1]);
                        break;
                    case "-packageUUID":
                        packageUUID = UUID.fromString(args[i + 1]);
                        break;
                    case "-regionCode":
                        regionCode = Integer.parseInt(args[i + 1]);
                        break;
                    case "-cer":
                        sourceId = args[i + 1];
                        break;
                    case "-hash":
                        hash = args[i + 1];
                        break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Недостаточно аргументов.");
            return;
        } catch (Exception e) {
            System.out.println("Неверный формат аргумента. " + e.getMessage());
            return;
        }

        KOSPackage KOSPackage = appManager.packageCreateHelper(isUploaded,
                file,
                docTypeCode,
                docTypeVersion,
                attachmentUUID,
                packageUUID,
                regionCode,
                hash,
                sourceId);

        // не удалось сформировать пакет
        if (KOSPackage == null)
            return;

        BaseRequest request;
        try {
            if (mode == 0) {
                request = new DisabledDataRequestAdd(KOSPackage);
            } else {
                request = new DisabledDataRequestUpdate(KOSPackage);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        appManager.sendResponse(MainSMEVPage.DISABLED_DATA_REQUEST, request);
    }

    public static void showHelp() {
        System.out.println("Использование:");
        System.out.println("java -jar autoregresstest.jar <args>");
        System.out.println("args:");
        System.out.println("add|update <isUploaded> <zipFilePath> <docTypeCode> <docTypeVersion> " +
                "[-attachUUID <attachmentUUID>] [-packageUUID <packageUUID>] [-regionCode <regionCode>] [-hash <md5Hash>] [-cer <sourceID>]");
        System.out.println("delete <packageUUID>");
        System.out.println("extract <snils> <validOn> <dateFrom> <dateTo>");
        System.out.println();
        System.out.println("Формат boolean (isUploaded): true|false");
        System.out.println("Формат даты: yyyy-MM-dd");
        System.out.println("Формат СНИЛС: XXX-XXX-XXX XX|XXX XXX XXX XX|XXXXXXXXXXX");
    }
}
