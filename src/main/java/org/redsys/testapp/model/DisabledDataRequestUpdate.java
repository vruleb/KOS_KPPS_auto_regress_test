package org.redsys.testapp.model;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class DisabledDataRequestUpdate extends DisabledDataRequest {

    public DisabledDataRequestUpdate(ZipPackage zipPackage) throws FileNotFoundException {
        initRequest(zipPackage);
    }

    private void initRequest(ZipPackage zipPackage) throws FileNotFoundException {

        String requestTemplate = loadTemplate(new File("./src/main/resources/RequestTemplates/DisabledDataRequest/update.xml"));

        requestTemplate = replaceAll(requestTemplate,"<!--packageUUID-->", zipPackage.getPackageUUID().toString());
        requestTemplate = replaceAll(requestTemplate,"<!--docTypeCode-->", zipPackage.getDocTypeCode());
        requestTemplate = replaceAll(requestTemplate,"<!--docTypeVersion-->", zipPackage.getDocTypeCode());
        requestTemplate = replaceAll(requestTemplate,"<!--attachmentUUID-->", zipPackage.getAttachmentUUID().toString());
        requestTemplate = replaceAll(requestTemplate,"<!--fileName-->", zipPackage.getFile().getName());
        requestTemplate = replaceAll(requestTemplate,"<!--hash-->", zipPackage.getMd5hash());

        Integer regionCode = zipPackage.getRegionCode();
        if (regionCode != null) {
            String regionCodeVar = "<tns:RegionCode>" + regionCode + "</tns:RegionCode>";
            requestTemplate = replaceAll(requestTemplate,"<!--regionCodeVar-->", regionCodeVar);
        }

        //установить значение
        this.setRequestStr(requestTemplate);
    }
}
