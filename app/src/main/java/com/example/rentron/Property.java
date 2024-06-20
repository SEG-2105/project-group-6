package com.example.rentron;

public class Property {
    private String address;
    private String type;
    private int floor;
    private int numRooms;
    private int numBathrooms;
    private int numFloors;
    private int totalArea;
    private boolean laundryInUnit;
    private int parkingSpots;
    private double totalRent;
    private boolean utilitiesHydro;
    private boolean utilitiesHeating;
    private boolean utilitiesWater;

    public Property() {
    }

    public Property(String address, String type, int floor, int numRooms, int numBathrooms, int numFloors, int totalArea, boolean laundryInUnit, int parkingSpots, double totalRent, boolean utilitiesHydro, boolean utilitiesHeating, boolean utilitiesWater) {
        this.address = address;
        this.type = type;
        this.floor = floor;
        this.numRooms = numRooms;
        this.numBathrooms = numBathrooms;
        this.numFloors = numFloors;
        this.totalArea = totalArea;
        this.laundryInUnit = laundryInUnit;
        this.parkingSpots = parkingSpots;
        this.totalRent = totalRent;
        this.utilitiesHydro = utilitiesHydro;
        this.utilitiesHeating = utilitiesHeating;
        this.utilitiesWater = utilitiesWater;
    }

    // Getters and setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getNumRooms() { return numRooms; }
    public void setNumRooms(int numRooms) { this.numRooms = numRooms; }

    public int getNumBathrooms() { return numBathrooms; }
    public void setNumBathrooms(int numBathrooms) { this.numBathrooms = numBathrooms; }

    public int getNumFloors() { return numFloors; }
    public void setNumFloors(int numFloors) { this.numFloors = numFloors; }

    public int getTotalArea() { return totalArea; }
    public void setTotalArea(int totalArea) { this.totalArea = totalArea; }

    public boolean isLaundryInUnit() { return laundryInUnit; }
    public void setLaundryInUnit(boolean laundryInUnit) { this.laundryInUnit = laundryInUnit; }

    public int getParkingSpots() { return parkingSpots; }
    public void setParkingSpots(int parkingSpots) { this.parkingSpots = parkingSpots; }

    public double getTotalRent() { return totalRent; }
    public void setTotalRent(double totalRent) { this.totalRent = totalRent; }

    public boolean isUtilitiesHydro() { return utilitiesHydro; }
    public void setUtilitiesHydro(boolean utilitiesHydro) { this.utilitiesHydro = utilitiesHydro; }

    public boolean isUtilitiesHeating() { return utilitiesHeating; }
    public void setUtilitiesHeating(boolean utilitiesHeating) { this.utilitiesHeating = utilitiesHeating; }

    public boolean isUtilitiesWater() { return utilitiesWater; }
    public void setUtilitiesWater(boolean utilitiesWater) { this.utilitiesWater = utilitiesWater; }
}
