package org.redsys.testapp.model;

import java.io.File;
import java.io.IOException;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class DisabledDataRequestAdd extends DisabledDataRequest {

    public DisabledDataRequestAdd(KOSPackage KOSPackage) throws IOException {
        initRequest(KOSPackage);
    }

    private void initRequest(KOSPackage KOSPackage) throws IOException {
        String requestTemplate = loadResource(new File("./RequestTemplates/DisabledDataRequest/add.xml"));
        String sourceId = KOSPackage.getSourceId();
        String certificate;
        if (sourceId == null) {
            certificate = loadResource(new File("./Certificates/pfr.cer"));
        } else {
            certificate = loadResource(new File("./Certificates/" + sourceId.toLowerCase() + ".cer"));
        }

        requestTemplate = replaceAll(requestTemplate, "<!--packageUUID-->", KOSPackage.getPackageUUID().toString());
        requestTemplate = replaceAll(requestTemplate, "<!--docTypeCode-->", KOSPackage.getDocTypeCode());
        requestTemplate = replaceAll(requestTemplate, "<!--docTypeVersion-->", KOSPackage.getDocTypeVersion());
        requestTemplate = replaceAll(requestTemplate, "<!--attachmentUUID-->", KOSPackage.getAttachmentUUID().toString());
        requestTemplate = replaceAll(requestTemplate, "<!--fileName-->", KOSPackage.getFile().getName());
        requestTemplate = replaceAll(requestTemplate, "<!--hash-->", KOSPackage.getMd5hash());
        requestTemplate = replaceAll(requestTemplate, "<!--certificate-->", certificate);


        Integer regionCode = KOSPackage.getRegionCode();
        if (regionCode != null) {
            String regionCodeVar = "<tns:RegionCode>" + regionCode + "</tns:RegionCode>";
            requestTemplate = replaceAll(requestTemplate, "<!--regionCodeVar-->", regionCodeVar);
        }

        //установить значение
        this.setRequestStr(requestTemplate);
    }
}
