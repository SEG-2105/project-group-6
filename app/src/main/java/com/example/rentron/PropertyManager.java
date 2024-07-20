package com.example.rentron;

public class PropertyManager {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private double averageRating;
    private int propertiesManaged;
    private int ticketsHandled;

    public PropertyManager() {}

    public PropertyManager(String id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getPropertiesManaged() {
        return propertiesManaged;
    }

    public void setPropertiesManaged(int propertiesManaged) {
        this.propertiesManaged = propertiesManaged;
    }

    public int getTicketsHandled() {
        return ticketsHandled;
    }

    public void setTicketsHandled(int ticketsHandled) {
        this.ticketsHandled = ticketsHandled;
    }
}
