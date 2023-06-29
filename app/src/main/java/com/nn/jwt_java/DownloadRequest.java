package com.nn.jwt_java;

import java.util.ArrayList;

public class DownloadRequest {

    private String deviceSn;
    private String modelId;
    private boolean compress;
    private ArrayList<Integer> configFiles;

    public String getDeviceSn() {
        return deviceSn;
    }

    public String getModelId() {
        return modelId;
    }

    public boolean isCompress() {
        return compress;
    }

    public ArrayList<Integer> getConfigFiles() {
        return configFiles;
    }

    public DownloadRequest(String deviceSn, String modelId, boolean compress, ArrayList<Integer> configFiles) {
        this.deviceSn = deviceSn;
        this.modelId = modelId;
        this.compress = compress;
        this.configFiles = configFiles;
    }
}
