package com.example.rentron.data.models;

import android.util.Log;

import com.example.rentron.data.models.properties.Property;
import com.example.rentron.data.models.requests.LandlordInfo;
import com.example.rentron.data.models.requests.ClientInfo;
import com.example.rentron.data.models.requests.PropertyInfo;
import com.example.rentron.utils.Utilities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {

    private String requestID;
    private LandlordInfo landlordInfo;
    private ClientInfo clientInfo;
    private Map<String, PropertyInfo> properties;
    private Date date; // request date
    private boolean isPending;
    private boolean isRejected;
    private boolean isCompleted;
    private boolean isRated;
    private double rating;
    private boolean ticketSubmitted;

    // Constructor Methods--------------------------------------------------------------------------------------

    /**
     * Constructor to initialize an empty request
     */
    public Request() {
        this.date = Utilities.getTodaysDate();
        this.properties = new HashMap<>();
        this.setIsPending(true);
        this.setIsRejected(false);
        this.setIsCompleted(false);
        this.setIsRated(false);
        this.setRating(0);
        this.setTicketSubmitted(false);
    }

    /**
     * constructor method
     * @param requestID ID of the request
     * @param landlordInfo info of landlord who makes the properties
     * @param clientInfo info of the client who placed the request
     * @param properties propertyInfo and corresponding quantity of each property in request
     * @param date Date and time for when the request was placed
     * @param isRated whether the request's already been rated or not
     * @param rating the rating for this request
     */
    public Request(String requestID, LandlordInfo landlordInfo, ClientInfo clientInfo, Map<String, PropertyInfo> properties, Date date, boolean isRated, double rating){
        // Initialization
        this.setRequestID(requestID);
        this.setLandlordInfo(landlordInfo);
        this.setClientInfo(clientInfo);
        this.setProperties(properties);
        this.setDate(date);
        this.setIsPending(true);
        this.setIsRejected(false);
        this.setIsCompleted(false);
        this.setIsRated(isRated);
        this.setRating(rating);
        this.setTicketSubmitted(false);
    }

    //----------------------------------------------------------------------------------------------------------

    // Landlord Info

    public void setLandlordInfo(LandlordInfo landlordInfo){
        this.landlordInfo = landlordInfo;
    }

    public LandlordInfo getLandlordInfo(){
        return this.landlordInfo;
    }

    // Client Info

    public void setClient(Client client) {
        this.clientInfo = new ClientInfo(client);
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    //----------------------------------------------------------------------------------------------------------
    /**
     * Sets the value of the propertyQuantity map
     * @param properties map of propertyInfo and corresponding quantity
     */
    public void setProperties(Map<String, PropertyInfo> properties) {
        this.properties = properties;
    }

    public void addPropertyQuantity(Property property, Integer quantity) {

        PropertyInfo propertyInfo = new PropertyInfo(property);
        propertyInfo.setQuantity(quantity);

        this.properties.put(property.getPropertyID(), propertyInfo);
    }

    public Map<String, PropertyInfo> getProperties() {
        return properties;
    }

    //----------------------------------------------------------------------------------------------------------

    public boolean isRated() {
        return isRated;
    }

    public void setIsRated(boolean isRated) {
        this.isRated = isRated;
    }

    //----------------------------------------------------------------------------------------------------------

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    //----------------------------------------------------------------------------------------------------------

    public void setIsRejected(boolean rejected) {
        isRejected = rejected;
    }

    //----------------------------------------------------------------------------------------------------------

    public boolean isTicketSubmitted(){return ticketSubmitted;}

    public void setTicketSubmitted(boolean ticketSubmitted){ this.ticketSubmitted = ticketSubmitted;}
    /**
     * Sets the value of the request ID
     * @param requestID
     */
    public void setRequestID(String requestID){
        this.requestID = requestID;
    }

    /**
     * Return the ID of the request
     * @return request ID
     */
    public String getRequestID(){
        return this.requestID;
    }

    //----------------------------------------------------------------------------------------------------------
    /**
     * Set the date that the request was placed
     * @param date
     */
    public void setDate(Date date){
        this.date = date;
    }

    /**
     * Return the date and time that the request was placed
     * @return date of request placed
     */
    public Date getRequestDate(){
        return this.date;
    }

    //----------------------------------------------------------------------------------------------------------
    /**
     * Set the status of pending
     * @param isPending
     */
    public void setIsPending(boolean isPending){
        this.isPending = isPending;
    }

    /**
     * Returns pending status
     * @return isPending
     */
    public boolean getIsPending(){
        return this.isPending;
    }

    //----------------------------------------------------------------------------------------------------------
    /**
     * Return rejection status
     * @return isRejected
     */
    public boolean getIsRejected(){
        return this.isRejected;
    }

    //----------------------------------------------------------------------------------------------------------
    /**
     * Set the status of completed
     * @param isCompleted
     */
    public void setIsCompleted(boolean isCompleted){
        this.isCompleted = isCompleted;
    }

    /**
     * Return completion status
     * @return isCompleted
     */
    public boolean getIsCompleted(){
        return this.isCompleted;
    }

}
