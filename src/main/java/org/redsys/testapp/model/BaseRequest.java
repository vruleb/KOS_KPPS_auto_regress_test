package org.redsys.testapp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
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

    protected String loadTemplate(File resource) throws FileNotFoundException {
        return new Scanner(resource).useDelimiter("\\Z").next();
    }

    protected String replaceAll(String in, String oldStr, String newStr) {
        if (oldStr == null || newStr == null) {
            return in;
        }
        return in.replaceAll(Pattern.quote(oldStr), Matcher.quoteReplacement(newStr));
    }
}
