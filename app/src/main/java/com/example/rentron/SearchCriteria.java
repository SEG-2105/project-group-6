package com.example.rentron;

public class SearchCriteria {
    private String type;
    private int minRooms;
    private int minBathrooms;
    private int minFloors;
    private double minArea;
    private double minRent;
    private double maxRent;
    private boolean[] utilitiesIncluded;
    private int minParkingSpots;

    public SearchCriteria() {}

    public SearchCriteria(String type, int minRooms, int minBathrooms, int minFloors, double minArea, double minRent, double maxRent, boolean[] utilitiesIncluded, int minParkingSpots) {
        this.type = type;
        this.minRooms = minRooms;
        this.minBathrooms = minBathrooms;
        this.minFloors = minFloors;
        this.minArea = minArea;
        this.minRent = minRent;
        this.maxRent = maxRent;
        this.utilitiesIncluded = utilitiesIncluded;
        this.minParkingSpots = minParkingSpots;
    }

    public boolean matches(Property property) {
        // Implement the logic to match the property with the criteria
        boolean matchesType = type == null || type.isEmpty() || type.equals(property.getType());
        boolean matchesRooms = property.getRooms() >= minRooms;
        boolean matchesBathrooms = property.getBathrooms() >= minBathrooms;
        boolean matchesFloors = property.getFloors() >= minFloors;
        boolean matchesArea = property.getArea() >= minArea;
        boolean matchesRent = property.getRent() >= minRent && property.getRent() <= maxRent;
        boolean matchesParking = property.getParkingSpots() >= minParkingSpots;
        boolean matchesUtilities = true;

        for (int i = 0; i < utilitiesIncluded.length; i++) {
            if (utilitiesIncluded[i] && !property.getUtilitiesIncluded()[i]) {
                matchesUtilities = false;
                break;
            }
        }

        return matchesType && matchesRooms && matchesBathrooms && matchesFloors && matchesArea && matchesRent && matchesParking && matchesUtilities;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinRooms() {
        return minRooms;
    }

    public void setMinRooms(int minRooms) {
        this.minRooms = minRooms;
    }

    public int getMinBathrooms() {
        return minBathrooms;
    }

    public void setMinBathrooms(int minBathrooms) {
        this.minBathrooms = minBathrooms;
    }

    public int getMinFloors() {
        return minFloors;
    }

    public void setMinFloors(int minFloors) {
        this.minFloors = minFloors;
    }

    public double getMinArea() {
        return minArea;
    }

    public void setMinArea(double minArea) {
        this.minArea = minArea;
    }

    public double getMinRent() {
        return minRent;
    }

    public void setMinRent(double minRent) {
        this.minRent = minRent;
    }

    public double getMaxRent() {
        return maxRent;
    }

    public void setMaxRent(double maxRent) {
        this.maxRent = maxRent;
    }

    public boolean[] getUtilitiesIncluded() {
        return utilitiesIncluded;
    }

    public void setUtilitiesIncluded(boolean[] utilitiesIncluded) {
        this.utilitiesIncluded = utilitiesIncluded;
    }

    public int getMinParkingSpots() {
        return minParkingSpots;
    }

    public void setMinParkingSpots(int minParkingSpots) {
        this.minParkingSpots = minParkingSpots;
    }
}
