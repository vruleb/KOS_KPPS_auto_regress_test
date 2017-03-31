package org.redsys.testapp.model;

import java.io.File;
import java.io.IOException;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class DisabledDataRequestDelete extends DisabledDataRequest {

    public DisabledDataRequestDelete(String packageUUID) throws IOException {
        initRequest(packageUUID);
    }

    private void initRequest(String packageUUID) throws IOException {
        String requestTemplate = loadResource(new File("./RequestTemplates/DisabledDataRequest/delete.xml"));
        String certificate = loadResource(new File("./Certificates/emulator.cer"));

        requestTemplate = replaceAll(requestTemplate, "<!--packageUUID-->", packageUUID);
        requestTemplate = replaceAll(requestTemplate, "<!--certificate-->", certificate);
        //установить значение
        this.setRequestStr(requestTemplate);
    }
}
