package com.example.rentron;

public class RentalRequest {
    private String id;
    private String clientId;
    private String propertyId;
    private String status;

    public RentalRequest() {}

    public RentalRequest(String id, String clientId, String propertyId, String status) {
        this.id = id;
        this.clientId = clientId;
        this.propertyId = propertyId;
        this.status = status;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
