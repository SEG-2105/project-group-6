package com.example.rentron;

public class RentalRequest {
    private String clientEmail;
    private String propertyAddress;
    private boolean resolved;
    private boolean rejected;

    public RentalRequest(String clientEmail, String propertyAddress) {
        this.clientEmail = clientEmail;
        this.propertyAddress = propertyAddress;
        this.resolved = false;
        this.rejected = false;
    }

    // Getters and setters
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public boolean isRejected() { return rejected; }
    public void setRejected(boolean rejected) { this.rejected = rejected; }
}
