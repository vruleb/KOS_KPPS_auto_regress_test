package org.redsys.testapp.applogic;

import org.apache.commons.net.ftp.FTPClient;
import org.redsys.testapp.model.ZipPackage;

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

    public void connect(InetAddress host, int port, String user, String pass) throws IOException {
        if (!ftpClient.isConnected()) {
            ftpClient.connect(host, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
        }
    }

    public void closeConnection() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    public void uploadPackage(ZipPackage zipPackage) {

        String dir = zipPackage.getAttachmentUUID().toString();
        String fileName = zipPackage.getFile().getName();
        String remoteFilePath = dir + "/" + fileName;

        try {
            ftpClient.makeDirectory(dir);
            InputStream inputStream = new FileInputStream(zipPackage.getFile());

            System.out.println("Start uploading file " + fileName + " into " + dir);
            boolean done = ftpClient.storeFile(remoteFilePath, inputStream);
            inputStream.close();
            if (done) {
                System.out.println("The file is uploaded successfully.");
            }

            // обновление данных о пакете
            zipPackage.setAttachmentUUID(UUID.randomUUID());
            zipPackage.setMd5hash(getMD5Checksum(zipPackage.getFile()));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void deletePackage(UUID attachmentUUID, String fileName) {

        String dir = attachmentUUID.toString();
        String remoteFilePath = dir + "/" + fileName;

        try {
            ftpClient.deleteFile(remoteFilePath);
            //удалить директорию, если она пуста
            ftpClient.removeDirectory(dir);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getRemoteFileHash(String fileName, UUID attachmentUUID) {

        File tmpFile;
        try {
            tmpFile = File.createTempFile("package", ".zip");
            downloadFile(fileName, attachmentUUID, tmpFile);
            return getMD5Checksum(tmpFile);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean downloadFile(String fileName, UUID attachmentUUID, File localFile) {
        String remoteFilePath = attachmentUUID + "/" + fileName;
        try {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFilePath);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream.write(bytesArray, 0, bytesRead);
            }

            boolean success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File has been downloaded successfully.");
            }
            outputStream.close();
            inputStream.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getMD5Checksum(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        fis.close();
        return md5;
    }
}
