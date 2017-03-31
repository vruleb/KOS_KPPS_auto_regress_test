package org.redsys.testapp.model;

import java.io.File;

import java.util.UUID;

/**
 * Created by vyacheslav.vrubel on 20.03.2017.
 */
public class ZipPackage {

    private File file;              //путь к файлу
    private String docTypeCode;     //формат пакета
    private String docTypeVersion;  //версия формата пакета
    private Integer regionCode;     //код региона
    private String sourceId;        //поставщик сведений

    private UUID packageUUID;       //UUID пакета
    private UUID attachmentUUID;    //директория, в которой находится/будет находиться пакет

    private String md5hash;

    public void setPackageUUID(UUID packageUUID) {
        this.packageUUID = packageUUID;
    }

    public void setAttachmentUUID(UUID attachmentUUID) {
        this.attachmentUUID = attachmentUUID;
    }

    public ZipPackage(File file, String docTypeCode, String docTypeVersion, Integer regionCode, String sourceId,
                      UUID packageUUID, UUID attachmentUUID, String md5hash) {
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

    public UUID getPackageUUID() {
        return packageUUID;
    }

    public UUID getAttachmentUUID() {
        return attachmentUUID;
    }

    public void setMd5hash(String md5hash) {
        this.md5hash = md5hash;
    }

    public String getMd5hash() {
        return md5hash;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
}
