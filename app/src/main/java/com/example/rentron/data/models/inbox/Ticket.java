package com.example.rentron.data.models.inbox;

import com.example.rentron.data.entity_models.TicketEntityModel;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Ticket class to create instances of tickets which will be stored in an inbox
 * Implements Comparator to enable sorting of tickets by date submitted
 */
public class Ticket implements Comparator<Ticket>, Serializable {
    private String errorMsg = "";

    // instance variables
    private String id;
    private String title;
    private String description;
    private String clientId;
    private String landlordId;
    private String requestId;
    private boolean isResolved;
    private Date dateSubmitted;

    /**
     * Using enum to define property names of a ticket in a structured (and more rigid) manner
     * Prevents use of hard-coded string throughout application where property of a ticket is to be used
     * Used in InboxActions, as property names here are same as field names on firebase
     */
    public enum TICKET_PROPERTY {
        id,
        title,
        description,
        clientId,
        landlordId,
        dateSubmitted
    }

    /**
     * Constructor to create a new ticket instance by providing values for all instance variables
     * @param id ticket id
     * @param title title of ticket
     * @param description description of ticket
     * @param clientId id of client who submitted the ticket
     * @param landlordId id of landlord regarding whom ticket has been submitted
     * @param dateSubmitted date on which ticket is submitted
     */
    public Ticket(String id, String title, String description, String clientId, String landlordId, Date dateSubmitted) {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setClientId(clientId);
        this.setLandlordId(landlordId);
        this.setDateSubmitted(dateSubmitted);
        this.setResolved(false);
    }

    public Ticket(String id, String title, String description, String clientId, String landlordId, String dateSubmitted) throws ParseException {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setClientId(clientId);
        this.setLandlordId(landlordId);
        this.setDateSubmitted(dateSubmitted);
        this.setResolved(false);
    }

    /**
     * Constructor to create a new ticket instance by providing TicketEntityModel
     * @param ticketData TicketEntityModel containing unvalidated data
     */
    public Ticket(TicketEntityModel ticketData) throws ParseException {
//        setId(ticketData.getId());
        this.setTitle(ticketData.getTitle());
        this.setDescription(ticketData.getDescription());
        this.setClientId(ticketData.getClientId());
        this.setLandlordId(ticketData.getLandlordId());
        // set the date submitted, receives value as string, throws ParseException if format is incorrect
        this.setDateSubmitted(ticketData.getDateSubmitted());
        this.setResolved(false);
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

        // Process: validating the title
        if (validateName(title)) { //valid

            this.title = title; //setting title

        }
        else { //invalid

            // Output: error message
            throw new IllegalArgumentException(errorMsg);

        }

    }

    /**
     * this helper method validates the title and checks that it's not empty
     * @param title the title of the ticket
     * @return whether the title is valid or not
     */
    private boolean validateName(String title) {

        // Process: checking title length
        if (title.length() > 0) { //at least 1 char

            if (title.length() > 50) { //too long

                errorMsg = "Please limit the title to 50 characters"; //updating error msg

                return false;

            }

            return true;

        }
        else { //nothing inputted

            errorMsg = "Ticket must be titled"; //updating error msg

            return false;

        }

    }

    public String getDescription() {
        return description;
    }

    /**
     * Set/Change the description of the ticket
     * @param description of ticket
     */
    public void setDescription(String description) {

        if (description.length() >= 20) { //valid

            this.description = description;

        }
        else { //too short or nothing inputted

            throw new IllegalArgumentException("Description should be at least 20 characters long");

        }

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    private void setDateSubmitted(String dateSubmitted) throws ParseException {
        // mm-dd--yyyy
        // this.dateSubmitted = Utilities.getDateFromString(dateSubmitted);
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public Map<String, Object> getTicketDataMap() {
        HashMap<String, Object> ticketDataMap = new HashMap<>();
        ticketDataMap.put(TICKET_PROPERTY.title.toString(), this.title);
        ticketDataMap.put(TICKET_PROPERTY.description.toString(), this.description);
        ticketDataMap.put(TICKET_PROPERTY.clientId.toString(), this.clientId);
        ticketDataMap.put(TICKET_PROPERTY.landlordId.toString(), this.landlordId);
        ticketDataMap.put(TICKET_PROPERTY.dateSubmitted.toString(), this.dateSubmitted);
        return ticketDataMap;
    }

    /**
     * Allows ticket instances to be sorted by date submitted
     * @return 0 if submitted on same date, -1 is ticket1 was submitted earlier, 1 if ticket1 was submitted later
     */
    @Override
    public int compare(Ticket ticket1, Ticket ticket2) {
        return ticket2.getDateSubmitted().compareTo(ticket1.getDateSubmitted());
    }
}
