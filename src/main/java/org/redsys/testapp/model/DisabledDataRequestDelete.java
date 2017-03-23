package org.redsys.testapp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class DisabledDataRequestDelete extends DisabledDataRequest {

    public DisabledDataRequestDelete(UUID packageUUID) throws FileNotFoundException {
        initRequest(packageUUID);
    }

    private void initRequest(UUID packageUUID) throws FileNotFoundException {
        String requestTemplate = loadTemplate(new File("./src/main/resources/RequestTemplates/DisabledDataRequest/delete.xml"));

        requestTemplate = replaceAll(requestTemplate,"<!--packageUUID-->", packageUUID.toString());
        //установить значение
        this.setRequestStr(requestTemplate);
    }
}
