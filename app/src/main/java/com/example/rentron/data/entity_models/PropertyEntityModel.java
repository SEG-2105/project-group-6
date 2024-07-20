package com.example.rentron.data.entity_models;

import java.util.ArrayList;

public class PropertyEntityModel {

    private String address;
    private String propertyID;
    private String landlordID;
    private Integer rooms;
    private String propertyType;
    private Integer bathrooms;
    private ArrayList<String> amenities;
    private Integer floors;
    private boolean laundry;
    private Integer parking;
    private boolean offered;
    private double price;

    public PropertyEntityModel() {}

    public PropertyEntityModel(String propertyID, String landlordID, String address, Integer rooms, String propertyType,
                               Integer bathrooms, ArrayList<String> amenities, Integer floors, boolean laundry, Integer parking, boolean offered, double price) {
        this.propertyID = propertyID;
        this.landlordID = landlordID;
        this.address = address;
        this.rooms = rooms;
        this.propertyType = propertyType;
        this.bathrooms = bathrooms;
        this.amenities = amenities;
        this.floors = floors;
        this.laundry = laundry;
        this.parking = parking;
        this.offered = offered;
        this.price = price;
    }

    // Getters and Setters

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public String getLandlordID() {
        return landlordID;
    }

    public void setLandlordID(String landlordID) {
        this.landlordID = landlordID;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public ArrayList<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(ArrayList<String> amenities) {
        this.amenities = amenities;
    }

    public Integer getFloors() {
        return floors;
    }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public boolean isLaundry() {
        return laundry;
    }

    public void setLaundry(boolean laundry) {
        this.laundry = laundry;
    }

    public Integer getParking() {
        return parking;
    }

    public void setParking(Integer parking) {
        this.parking = parking;
    }

    public boolean isOffered() {
        return offered;
    }

    public void setOffered(boolean offered) {
        this.offered = offered;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
