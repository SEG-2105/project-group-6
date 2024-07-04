package com.example.rentron;

public class Ticket {
    private String propertyAddress;
    private String type;
    private String message;
    private int urgency;
    private String clientEmail;
    private boolean resolved;

    public Ticket(String propertyAddress, String type, String message, int urgency, String clientEmail) {
        this.propertyAddress = propertyAddress;
        this.type = type;
        this.message = message;
        this.urgency = urgency;
        this.clientEmail = clientEmail;
        this.resolved = false;
    }

    // Getters and setters
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getUrgency() { return urgency; }
    public void setUrgency(int urgency) { this.urgency = urgency; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
}
