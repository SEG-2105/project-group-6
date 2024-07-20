package com.example.rentron.data.models.requests;

import com.example.rentron.data.models.Address;
import com.example.rentron.data.models.Landlord;

import java.io.Serializable;

public class LandlordInfo implements Serializable {
    String landlordId;
    String landlordName;
    String landlordDescription;
    double landlordRating;
    Address landlordAddress;

    public LandlordInfo(String landlordId, String landlordName, String landlordDescription, double landlordRating, Address landlordAddress) {
        this.setLandlordId(landlordId);
        this.setLandlordName(landlordName);
        this.setLandlordDescription(landlordDescription);
        this.setLandlordRating(landlordRating);
        this.setLandlordAddress(landlordAddress);
    }

    public LandlordInfo(Landlord landlord) {
        this.setLandlordId(landlord.getUserId());
        this.setLandlordName(landlord.getFirstName() + " " + landlord.getLastName());
        this.setLandlordDescription(landlord.getDescription());
        this.setLandlordRating(landlord.getLandlordRating());
        this.setLandlordAddress(landlord.getAddress());
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getLandlordName() {
        return landlordName;
    }

    public void setLandlordName(String landlordName) {
        this.landlordName = landlordName;
    }

    public double getLandlordRating() {
        return landlordRating;
    }

    public void setLandlordRating(double landlordRating) {
        this.landlordRating = landlordRating;
    }

    public Address getLandlordAddress() {
        return landlordAddress;
    }

    public void setLandlordAddress(Address landlordAddress) {
        this.landlordAddress = landlordAddress;
    }

    public String getLandlordDescription() {
        return landlordDescription;
    }

    public void setLandlordDescription(String landlordDescription) {
        this.landlordDescription = landlordDescription;
    }
}
