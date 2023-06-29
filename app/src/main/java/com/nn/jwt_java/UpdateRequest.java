package com.nn.jwt_java;

import java.util.ArrayList;

public class UpdateRequest {

    private String deviceSn;
    private String modelId;
    private String operationPointId;
    private int deviceTypeId;
    private String entityId;
    private String appId;
    private int appVersion;
    private ArrayList<ConfigFiles> configFiles;

    public UpdateRequest(String deviceSn, String modelId, String operationPointId, int deviceTypeId, String entityId, String appId, int appVersion, ArrayList<ConfigFiles> configFiles) {
        this.deviceSn = deviceSn;
        this.modelId = modelId;
        this.operationPointId = operationPointId;
        this.deviceTypeId = deviceTypeId;
        this.entityId = entityId;
        this.appId = appId;
        this.appVersion = appVersion;
        this.configFiles = configFiles;
    }
}
