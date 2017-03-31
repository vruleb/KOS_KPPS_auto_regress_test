package org.redsys.testapp.model;

import java.io.File;

/**
 * Created by vyacheslav.vrubel on 20.03.2017.
 */
public class KOSPackage {

    private File file;              //путь к файлу
    private String docTypeCode;     //формат пакета
    private String docTypeVersion;  //версия формата пакета
    private Integer regionCode;     //код региона
    private String sourceId;        //поставщик сведений
    private String packageUUID;       //UUID пакета
    private String attachmentUUID;    //UUID вложения
    private String md5hash;

    public KOSPackage(File file, String docTypeCode, String docTypeVersion, Integer regionCode, String sourceId,
                      String packageUUID, String attachmentUUID, String md5hash) {
        this.file = file;
        this.docTypeCode = docTypeCode;
        this.docTypeVersion = docTypeVersion;
        this.regionCode = regionCode;
        this.sourceId = sourceId;
        this.md5hash = md5hash;
        this.packageUUID = packageUUID;
        this.attachmentUUID = attachmentUUID;
    }

    public File getFile() {
        return file;
    }

    public String getDocTypeCode() {
        return docTypeCode;
    }

    public String getDocTypeVersion() {
        return docTypeVersion;
    }

    public Integer getRegionCode() {
        return regionCode;
    }

    public String getPackageUUID() {
        return packageUUID;
    }

    public void setPackageUUID(String packageUUID) {
        this.packageUUID = packageUUID;
    }

    public String getAttachmentUUID() {
        return attachmentUUID;
    }

    public void setAttachmentUUID(String attachmentUUID) {
        this.attachmentUUID = attachmentUUID;
    }

    public String getMd5hash() {
        return md5hash;
    }

    public void setMd5hash(String md5hash) {
        this.md5hash = md5hash;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
