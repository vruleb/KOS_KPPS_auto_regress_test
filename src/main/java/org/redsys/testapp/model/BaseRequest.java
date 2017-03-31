package org.redsys.testapp.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vyacheslav.vrubel on 20.03.2017.
 */
public abstract class BaseRequest {

    private String requestStr;     //готовый текст запроса

    public String getRequestStr() {
        return requestStr;
    }

    public void setRequestStr(String requestStr) {
        this.requestStr = requestStr;
    }

    protected String loadResource(File resource) throws IOException {
        String str = null;
        str = new String(Files.readAllBytes(resource.toPath()), StandardCharsets.UTF_8);
        return str;
    }

    protected String replaceAll(String in, String oldStr, String newStr) {
        if (oldStr == null || newStr == null) {
            return in;
        }
        return in.replaceAll(Pattern.quote(oldStr), Matcher.quoteReplacement(newStr));
    }
}
