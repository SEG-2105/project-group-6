package com.example.rentron;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ticket {
    private String id;
    private String propertyId;
    private String clientId;
    private String managerId;
    private String type;
    private String message;
    private int urgency;
    private String status;
    private List<String> history;

    public Ticket() {}

    public Ticket(String id, String propertyId, String clientId, String managerId, String type, String message, int urgency) {
        this.id = id;
        this.propertyId = propertyId;
        this.clientId = clientId;
        this.managerId = managerId;
        this.type = type;
        this.message = message;
        this.urgency = urgency;
        this.status = "To-Do";
        this.history = new ArrayList<>();
        this.history.add("Created at " + new Date());
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUrgency() {
        return urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
