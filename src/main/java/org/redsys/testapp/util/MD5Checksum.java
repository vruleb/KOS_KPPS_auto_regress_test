package org.redsys.testapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by vyacheslav.vrubel on 24.03.2017.
 */
public class MD5Checksum {
    public static String getMD5Checksum(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            fis.close();
            return md5;
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось рассчитать хеш, требуемый файл не найден. " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
