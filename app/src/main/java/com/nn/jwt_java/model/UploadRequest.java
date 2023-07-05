package com.nn.jwt_java.model;

import android.net.Uri;

import java.sql.Blob;

public class UploadRequest {

    private String deviceSn;
    private String modelId;
    private Integer fileType;
    private String fileName;
    private String fileDate;
    private Uri file;

    public String getDeviceSn() {
        return deviceSn;
    }

    public String getModelId() {
        return modelId;
    }

    public Integer getFileType() {
        return fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDate() {
        return fileDate;
    }

    public Uri getFile() {
        return file;
    }

    public UploadRequest(String deviceSn, String modelId, Integer fileType, String fileName, String fileDate, Uri file) {
        this.deviceSn = deviceSn;
        this.modelId = modelId;
        this.fileType = fileType;
        this.fileName = fileName;
        this.fileDate = fileDate;
        this.file = file;
    }
}
