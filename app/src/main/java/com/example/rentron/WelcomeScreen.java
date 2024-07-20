package com.example.rentron;

public class WelcomeScreen {
    private String role;

    public WelcomeScreen(String role) {
        this.role = role;
    }

    public void displayWelcomeMessage() {
        System.out.println("Welcome! You are logged in as " + role);
    }

    public void logOff() {
        UserService userService = new UserService();
        userService.logoutUser();
        System.out.println("You have been logged off.");
    }
}
