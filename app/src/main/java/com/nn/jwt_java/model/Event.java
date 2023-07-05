package com.nn.jwt_java.model;

import com.nn.jwt_java.EventType;

import java.util.Date;

public class Event {

    private Integer type;
    private String date;
    private String entityId;
    private String data;

    public Event(Integer type, String date, String entityId, String data) {
        this.type = type;
        this.date = date;
        this.entityId = entityId;
        this.data = data;
    }

    public Integer getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getData() {
        return data;
    }
}
