package org.redsys.testapp.model;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vyacheslav.vrubel on 24.03.2017.
 */
public class ExtractionInvalidDataRequest extends BaseRequest {

    public ExtractionInvalidDataRequest(String snils, Date validOn, Date dateFrom, Date dateTo) throws IOException {
        initRequest(snils, validOn, dateFrom, dateTo);
    }

    public void initRequest(String snils, Date validOn, Date dateFrom, Date dateTo) throws IOException {
        String requestTemplate = loadResource(new File("./RequestTemplates/ExtractionInvalidDataRequest/extract.xml"));
        String certificate = loadResource(new File("./Certificates/emulator.cer"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        requestTemplate = replaceAll(requestTemplate, "<!--SNILS-->", snils);
        requestTemplate = replaceAll(requestTemplate, "<!--validOn-->", dateFormat.format(validOn));
        requestTemplate = replaceAll(requestTemplate, "<!--dateFrom-->", dateFormat.format(dateFrom));
        requestTemplate = replaceAll(requestTemplate, "<!--dateTo-->", dateFormat.format(dateTo));
        requestTemplate = replaceAll(requestTemplate, "<!--certificate-->", certificate);

        //установить значение
        this.setRequestStr(requestTemplate);
    }
}
