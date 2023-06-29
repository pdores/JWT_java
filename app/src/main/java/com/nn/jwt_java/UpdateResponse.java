package com.nn.jwt_java;

import java.util.ArrayList;

public class UpdateResponse {

    private boolean forceCleanConfig;
    private ArrayList<ConfigFileUpdates> configFileUpdates;

    public boolean isForceCleanConfig() {
        return forceCleanConfig;
    }

    public ArrayList<ConfigFileUpdates> getConfigFileUpdates() {
        return configFileUpdates;
    }
}
