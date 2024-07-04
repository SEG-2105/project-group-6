package com.example.rentron;

import java.util.List;
import java.util.Scanner;

public class UserManagement {

    public static User registerUser(Scanner scanner, List<User> users) {
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("User Type (landlord/client/propertyManager): ");
        String userType = scanner.nextLine();

        User user = new User(firstName, lastName, email, password, address, userType);
        users.add(user);
        System.out.println("User registered successfully!");
        return user;
    }

    public static User loginUser(Scanner scanner, List<User> users) {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                System.out.println("Login successful! Welcome, " + user.getFirstName());
                return user;
            }
        }
        System.out.println("Login failed.");
        return null;
    }
}
