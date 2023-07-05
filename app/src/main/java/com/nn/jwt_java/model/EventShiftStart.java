package com.nn.jwt_java.model;

import java.util.Date;

public class EventShiftStart {

    private Integer eventType;
    private String eventDate;
    private String shiftId;
    private String entityId;
    private String employeeId;

    public EventShiftStart(Integer eventType, String eventDate, String shiftId, String entityId, String employeeId) {
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.shiftId = shiftId;
        this.entityId = entityId;
        this.employeeId = employeeId;
    }

    public Integer getEventType() {
        return eventType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getShiftId() {
        return shiftId;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getEmployeeId() {
        return employeeId;
    }
}
