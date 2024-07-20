package com.example.rentron;

public class Property {
    private String id;
    private String address;
    private String type;
    private int floor;
    private int rooms;
    private int bathrooms;
    private int floors;
    private double area;
    private boolean laundryInUnit;
    private int parkingSpots;
    private double rent;
    private boolean[] utilitiesIncluded;
    private String landlordId;
    private String managerId;
    private String clientId; // Add this field
    private boolean isOccupied;

    public Property() {}

    public Property(String id, String address, String type, int floor, int rooms, int bathrooms, int floors, double area, boolean laundryInUnit, int parkingSpots, double rent, boolean[] utilitiesIncluded, String landlordId) {
        this.id = id;
        this.address = address;
        this.type = type;
        this.floor = floor;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.floors = floors;
        this.area = area;
        this.laundryInUnit = laundryInUnit;
        this.parkingSpots = parkingSpots;
        this.rent = rent;
        this.utilitiesIncluded = utilitiesIncluded;
        this.landlordId = landlordId;
        this.isOccupied = false;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getFloors() {
        return floors;
    }

    public void setFloors(int floors) {
        this.floors = floors;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public boolean isLaundryInUnit() {
        return laundryInUnit;
    }

    public void setLaundryInUnit(boolean laundryInUnit) {
        this.laundryInUnit = laundryInUnit;
    }

    public int getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(int parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    public double getRent() {
        return rent;
    }

    public void setRent(double rent) {
        this.rent = rent;
    }

    public boolean[] getUtilitiesIncluded() {
        return utilitiesIncluded;
    }

    public void setUtilitiesIncluded(boolean[] utilitiesIncluded) {
        this.utilitiesIncluded = utilitiesIncluded;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(String landlordId) {
        this.landlordId = landlordId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }
}
