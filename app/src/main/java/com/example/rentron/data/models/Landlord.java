package com.example.rentron.data.models;

import com.example.rentron.data.entity_models.UserEntityModel;
import com.example.rentron.data.models.properties.Properties;
import java.util.Date;

/**
 * This class instantiates an instance of Landlord for Rentron App
 * Child Class of User
 */
public class Landlord extends User {
    private String description;
    private String voidCheque;
    private double landlordRatingSum; // sum of ratings
    private int numOfRatings; // number of ratings
    private int numOfRequestsSold;
    private boolean isSuspended;
    private Date suspensionDate;
    // storing Landlord's properties in an instance of Properties class which provides methods to work with a collection of properties
    // variable is public for accessibility, but also final
    public final Properties PROPERTIES;
    public final Requests REQUESTS;

    /**
     * Create a single instance of landlord
     * @param firstName First name of the landlord
     * @param lastName Last name of the landlord
     * @param email email of the landlord
     * @param password password for the landlord
     * @param address address of the landlord
     * @param role Role of the landlord
     * @param description Description of the landlord
     * @param voidCheque Chequing information of landlord
     * Menu of a landlord is stored in a HashMap
     */
    public Landlord(String firstName, String lastName, String email, String password, Address address,
                    UserRoles role, String description, String voidCheque, int numberOfPropertiesSold, double landlordRatingSum, int numOfRatings)  throws IllegalArgumentException {
        // instantiate Landlord's data members
        super(firstName, lastName, email, password, address, role);
        this.setDescription(description);
        this.setVoidCheque(voidCheque);
        this.setNumOfRequestsSold(numberOfPropertiesSold);
        this.setLandlordRatingSum(landlordRatingSum);
        this.setNumOfRatings(numOfRatings);
        // instantiate a properties object where Landlord's properties will be stored
        this.PROPERTIES = new Properties();
        this.isSuspended = false;
        this.suspensionDate = null;
        this.REQUESTS = new Requests();
    }

    public Landlord(UserEntityModel userData, Address address, String description, String voidCheque) throws IllegalArgumentException {
        // instantiate Landlord's data members
        super(userData, address);
        this.setDescription(description);
        this.setVoidCheque(voidCheque);
        //this.setNumOfRequestsSold(0);
        this.setLandlordRatingSum(0);
        this.setNumOfRatings(0);
        // instantiate a properties object where Landlord's properties will be stored
        this.PROPERTIES = new Properties();
        this.isSuspended = false;
        this.suspensionDate = null;
        this.REQUESTS = new Requests();
    }

    /**
     * Get a short description of the landlord
     * @return String representing landlord's description
     */
    public String getDescription() { return description; }

    /**
     * Set the landlord's description
     * @param description String representing the landlord's description
     */
    public void setDescription(String description) throws IllegalArgumentException {
        // validate description
        if (description.length() > 0)
            this.description = description;
        else
            throw new IllegalArgumentException("Please enter a description");
    }

    /**
     * Get chequing information about landlord
     * @return String representing void cheque of landlord
     */
    public String getVoidCheque() {
        return voidCheque;
    }

    /**
     * Set the landlord's void cheque
     * @param voidCheque String representing void cheque of landlord
     */
    public void setVoidCheque(String voidCheque) {
        // validate void cheque
        this.voidCheque = voidCheque;
    }

    /**
     * Get the average rating of a landlord
     * @return Integer representing landlord's overall rating
     */
    public double getLandlordRatingSum() { return landlordRatingSum; }

    /**
     * Set the landlord's rating sum
     * @param landlordRatingSum integer representing the landlord's rating
     */
    public void setLandlordRatingSum(double landlordRatingSum) {

        this.landlordRatingSum = landlordRatingSum;
    }

    /**
     * Add to the landlord's rating sum
     * @param landlordRating integer representing the landlord's rating
     */
    public void addToLandlordRatingSum(double landlordRating) {

        this.landlordRatingSum += landlordRating;
        this.numOfRatings ++;
    }

    /**
     * Get the number of ratings done for a landlord
     * @return Integer representing number of ratings
     */
    public double getNumOfRatings() { return numOfRatings; }

    /**
     * Set the landlord's number of ratings
     * @param numOfRatings integer representing the landlord's number of ratings
     */
    public void setNumOfRatings(int numOfRatings) {
        this.numOfRatings = numOfRatings;
    }

    public double getLandlordRating(){
        return landlordRatingSum/numOfRatings;
    }
    /**
     * Get the total number of requests sold by a landlord
     * @return Integer representing landlord's total sales
     */
    public int getNumOfRequestsSold() { return REQUESTS.getCompletedRequests().size(); }

    /**
     * Set the landlord's total requests sold
     * @param numOfRequestsSold integer representing the landlord's total sales
     */
    public void setNumOfRequestsSold(int numOfRequestsSold) {
        this.numOfRequestsSold = numOfRequestsSold;
    }

    /**
     * Get a true/false whether landlord is banned
     * @return boolean suspended or not
     */
    public boolean getIsSuspended() { return isSuspended; }

    /**
     * Set the boolean for suspended or not
     * @param isSuspended suspended or not
     */
    public void setIsSuspended(boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    /**
     * Get the end of suspension date
     * @return Date of end of suspension
     */
    public Date getSuspensionDate() { return suspensionDate; }

    /**
     * Set the boolean for suspended or not
     * @param suspensionDate date of end of suspension
     */
    public void setSuspensionDate(Date suspensionDate) {
        this.suspensionDate = suspensionDate;
    }

}
