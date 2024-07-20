package com.example.rentron.data.models.properties;

import com.example.rentron.data.entity_models.PropertyEntityModel;
import com.example.rentron.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a template/blueprint for each instance of a Property on a landlord's menu
 */
public class Property implements Serializable {
    private String errorMsg = "";

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
    private List<String> keywords; // Added this line

    /**
     * Create an instance of an existing property with a propertyID from FireBase
     * @param address Address of the property
     * @param propertyID ID number of the property
     * @param landlordID Landlord ID of the property
     * @param rooms Number of rooms in the property
     * @param propertyType Property Type (e.g., Apartment, House)
     * @param bathrooms Number of bathrooms in the property
     * @param amenities List of amenities in the property
     * @param floors Number of floors in the property
     * @param laundry Whether the property has laundry facilities
     * @param parking Number of parking spots available
     * @param offered Whether the property is currently offered or not
     * @param price Current price of the property
     */
    public Property(String address, String propertyID, String landlordID, Integer rooms, String propertyType,
                    Integer bathrooms, ArrayList<String> amenities, Integer floors, boolean laundry,
                    Integer parking, boolean offered, double price) {

        this.setAddress(address);
        this.setPropertyID(propertyID);
        this.setLandlordID(landlordID);
        this.setRooms(rooms);
        this.setPropertyType(propertyType);
        this.setBathrooms(bathrooms);
        this.setAmenities(amenities);
        this.setFloors(floors);
        this.setLaundry(laundry);
        this.setParking(parking);
        this.setOffered(offered);
        this.setPrice(price);
    }

    /**
     * Create an instance of property using PropertyEntityModel
     * @param propertyEntityModel property info to create
     */
    public Property(PropertyEntityModel propertyEntityModel) {

        this.setAddress(propertyEntityModel.getAddress());
        this.setPropertyID(propertyEntityModel.getPropertyID());
        this.setLandlordID(propertyEntityModel.getLandlordID());
        this.setRooms(propertyEntityModel.getRooms());
        this.setPropertyType(propertyEntityModel.getPropertyType());
        this.setBathrooms(propertyEntityModel.getBathrooms());
        this.setAmenities(propertyEntityModel.getAmenities());
        this.setFloors(propertyEntityModel.getFloors());
        this.setLaundry(propertyEntityModel.isLaundry());
        this.setParking(propertyEntityModel.getParking());
        this.setOffered(propertyEntityModel.isOffered());
        this.setPrice(propertyEntityModel.getPrice());
    }

    /**
     * Create an instance of a new property
     * @param address Address of the property
     * @param landlordID Landlord ID of the property
     * @param rooms Number of rooms in the property
     * @param propertyType Property Type (e.g., Apartment, House)
     * @param bathrooms Number of bathrooms in the property
     * @param amenities List of amenities in the property
     * @param floors Number of floors in the property
     * @param laundry Whether the property has laundry facilities
     * @param parking Number of parking spots available
     * @param offered Whether the property is currently offered or not
     * @param price Current price of the property
     */
    public Property(String address, String landlordID, Integer rooms, String propertyType, Integer bathrooms,
                    ArrayList<String> amenities, Integer floors, boolean laundry, Integer parking,
                    boolean offered, double price) {

        this.setAddress(address);
        this.setLandlordID(landlordID);
        this.setRooms(rooms);
        this.setPropertyType(propertyType);
        this.setBathrooms(bathrooms);
        this.setAmenities(amenities);
        this.setFloors(floors);
        this.setLaundry(laundry);
        this.setParking(parking);
        this.setOffered(offered);
        this.setPrice(price);
    }

    // Getters and Setters

    public String getAddress() { return address; }

    public void setAddress(String address) {
        if (validateAddress(address)) {
            this.address = address;
        } else {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    private boolean validateAddress(String address) {
        if (address.length() > 0) {
            return true;
        } else {
            errorMsg = "Address cannot be empty";
            return false;
        }
    }

    public String getPropertyID() { return propertyID; }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public String getLandlordID() { return landlordID; }

    public void setLandlordID(String landlordID) {
        this.landlordID = landlordID;
    }

    public Integer getRooms() { return rooms; }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getPropertyType() { return propertyType; }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getBathrooms() { return bathrooms; }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public ArrayList<String> getAmenities() { return amenities; }

    public void setAmenities(ArrayList<String> amenities) {
        this.amenities = amenities;
    }

    public Integer getFloors() { return floors; }

    public void setFloors(Integer floors) {
        this.floors = floors;
    }

    public boolean isLaundry() { return laundry; }

    public void setLaundry(boolean laundry) {
        this.laundry = laundry;
    }

    public Integer getParking() { return parking; }

    public void setParking(Integer parking) {
        this.parking = parking;
    }

    public boolean isOffered() { return offered; }

    public void setOffered(boolean offered) {
        this.offered = offered;
    }

    public double getPrice() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getSearchPropertyItemKeywords(String landlordName, String landlordAddress) {
        // store data first before keyword generation
        List<String> rawData = new ArrayList<>();

        // add Landlord's info, fields which are allowed as searchable

        // landlord name contains space separated firstname and lastname
        rawData.add(landlordName);
        rawData.add(landlordAddress);

        // add Property's info, fields which are allowed as searchable
        rawData.add(getAddress());
        rawData.add(getPropertyType());
        rawData.add(getRooms().toString());
        rawData.add(getBathrooms().toString());
        rawData.add(getAmenities().toString());
        rawData.add(getFloors().toString());
        rawData.add(Boolean.toString(isLaundry()));
        rawData.add(getParking().toString());
        rawData.add(Boolean.toString(isOffered()));
        rawData.add(Double.toString(getPrice()));

        // now that we have raw data, extract and return the keywords
        return Utilities.getKeywords(rawData);
    }
}
