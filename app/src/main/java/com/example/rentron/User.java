package com.example.rentron;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String uid;
    private String address;
    private String userType; // Add this if you need to store the user type (landlord, client, propertyManager)

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String firstName, String lastName, String email, String uid, String address, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.uid = uid;
        this.address = address;
        this.userType = userType;
    }

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
