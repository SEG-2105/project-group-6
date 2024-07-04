package com.example.rentron;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static List<User> users = new ArrayList<>();
    private static List<Property> properties = new ArrayList<>();
    private static List<RentalRequest> rentalRequests = new ArrayList<>();
    private static List<Ticket> tickets = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Rentron!");

        while (true) {
            System.out.println("\n1. Register\n2. Login\n3. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    User registeredUser = registerUser(scanner);
                    if (registeredUser != null) {
                        showWelcomeScreen(scanner, registeredUser);
                    }
                    break;
                case 2:
                    User loggedInUser = loginUser(scanner);
                    if (loggedInUser != null) {
                        showWelcomeScreen(scanner, loggedInUser);
                    }
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static User registerUser(Scanner scanner) {
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

    private static User loginUser(Scanner scanner) {
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

    private static void showWelcomeScreen(Scanner scanner, User user) {
        System.out.println("Hello, " + user.getFirstName() + "!");

        if ("landlord".equals(user.getUserType())) {
            PropertyManagement.showLandlordMenu(scanner, properties, users, user);
        } else if ("client".equals(user.getUserType())) {
            ClientManagement.showClientMenu(scanner, properties, rentalRequests, tickets, user);

            // Implement property manager menu as needed
        }
    }
}
