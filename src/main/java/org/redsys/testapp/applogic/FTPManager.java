package org.redsys.testapp.applogic;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.redsys.testapp.model.KOSPackage;
import org.redsys.testapp.util.MD5Checksum;

import java.io.*;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 21.03.2017.
 */
public class FTPManager {

    private FTPClient ftpClient;

    public FTPManager() {
        ftpClient = new FTPClient();
    }

    public void connect(InetAddress host, int port, String user, String pass) {
        if (!ftpClient.isConnected()) {
            try {
                ftpClient.connect(host, port);
                ftpClient.login(user, pass);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ftpClient.enterLocalPassiveMode();
        }
    }

    public void closeConnection() {

        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean uploadPackage(KOSPackage KOSPackage) {

        String dir = KOSPackage.getAttachmentUUID().toString();
        String fileName = KOSPackage.getFile().getName();
        String remoteFilePath = dir + "/" + fileName;

        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.makeDirectory(dir);
            InputStream inputStream = new FileInputStream(KOSPackage.getFile());

            boolean done = ftpClient.storeFile(remoteFilePath, inputStream);
            inputStream.close();
            return done;

        } catch (IOException ex) {
            ex.printStackTrace();
            //closeConnection();
            return false;
        }
    }

    public boolean deletePackage(UUID attachmentUUID, String fileName) {

        String dir = attachmentUUID.toString();
        String remoteFilePath = dir + "/" + fileName;

        try {
            return ftpClient.deleteFile(remoteFilePath);
            //удалить директорию, если она пуста
            //ftpClient.removeDirectory(dir);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRemoteFileHash(String fileName, UUID attachmentUUID) {

        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("package", ".zip");
        } catch (IOException e) {
            System.out.println("Не удалось создать временный файл для рассчета хеш-суммы.");
            e.printStackTrace();
            return null;
        }

        if (downloadFile(fileName, attachmentUUID, tmpFile)) {
            return MD5Checksum.getMD5Checksum(tmpFile);
        }
        return null;
    }

    public boolean downloadFile(String fileName, UUID attachmentUUID, File localFile) {
        String remoteFilePath = attachmentUUID + "/" + fileName;
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
            int returnCode = ftpClient.getReplyCode();
            if (inputStream == null || returnCode == 550) {
                System.out.println("Файл " + remoteFilePath + " не найден в хранилище.");
                return false;
            }
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream.write(bytesArray, 0, bytesRead);
            }

            boolean success = ftpClient.completePendingCommand();
            outputStream.close();
            inputStream.close();
            return success;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
