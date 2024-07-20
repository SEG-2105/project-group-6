package com.example.rentron.data.entity_models;

import java.util.Date;

/**
 * Entity Model to hold unvalidated client's complaint data
 */
public class TicketEntityModel {
    private String id;
    private String title;
    private String description;
    private String clientId;
    private String landlordId;
    private Date dateSubmitted;

    public TicketEntityModel(String id, String title, String description, String clientId, String landlordId, Date dateSubmitted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.clientId = clientId;
        this.landlordId = landlordId;
        this.dateSubmitted = dateSubmitted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }
}