package com.nn.jwt_java.model;

import java.util.ArrayList;

public class EventRequest {

    private String deviceSn;
    private String modelId;
    ArrayList<Event> events;

    public EventRequest(String deviceSn, String modelId, ArrayList<Event> events) {
        this.deviceSn = deviceSn;
        this.modelId = modelId;
        this.events = events;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public String getModelId() {
        return modelId;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
