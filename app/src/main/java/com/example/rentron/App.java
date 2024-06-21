package com.example.rentron.consoleapp;

import com.example.rentron.Property;
import com.example.rentron.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    private static List<User> users = new ArrayList<>();
    private static List<Property> properties = new ArrayList<>();

    public static void run() {
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
            showLandlordMenu(scanner, user);
        } else {
            showClientOrManagerMenu(scanner, user);
        }
    }

    private static void showLandlordMenu(Scanner scanner, User landlord) {
        while (true) {
            System.out.println("\nLandlord Menu:");
            System.out.println("1. Add Property\n2. View Properties\n3. Invite Property Manager\n4. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    addProperty(scanner, landlord);
                    break;
                case 2:
                    viewProperties(landlord);
                    break;
                case 3:
                    invitePropertyManager(scanner, landlord);
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showClientOrManagerMenu(Scanner scanner, User user) {
        while (true) {
            System.out.println("\n" + user.getUserType() + " Menu:");
            System.out.println("1. View Properties\n2. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    viewProperties(user);
                    break;
                case 2:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addProperty(Scanner scanner, User landlord) {
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Type (Basement, Studio, etc.): ");
        String type = scanner.nextLine();
        System.out.print("Floor: ");
        int floor = scanner.nextInt();
        System.out.print("Number of Rooms: ");
        int numRooms = scanner.nextInt();
        System.out.print("Number of Bathrooms: ");
        int numBathrooms = scanner.nextInt();
        System.out.print("Number of Floors: ");
        int numFloors = scanner.nextInt();
        System.out.print("Total Area: ");
        int totalArea = scanner.nextInt();
        System.out.print("Laundry In-Unit (true/false): ");
        boolean laundryInUnit = scanner.nextBoolean();
        System.out.print("Parking Spots: ");
        int parkingSpots = scanner.nextInt();
        System.out.print("Total Rent: ");
        double totalRent = scanner.nextDouble();
        System.out.print("Utilities Included - Hydro (true/false): ");
        boolean utilitiesHydro = scanner.nextBoolean();
        System.out.print("Utilities Included - Heating (true/false): ");
        boolean utilitiesHeating = scanner.nextBoolean();
        System.out.print("Utilities Included - Water (true/false): ");
        boolean utilitiesWater = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline

        Property property = new Property(address, type, floor, numRooms, numBathrooms, numFloors, totalArea, laundryInUnit, parkingSpots, totalRent, utilitiesHydro, utilitiesHeating, utilitiesWater);
        properties.add(property);
        System.out.println("Property added successfully!");
    }

    private static void viewProperties(User user) {
        System.out.println("Properties:");
        for (Property property : properties) {
            System.out.println(property.getAddress() + " - " + property.getType());
        }
    }

    private static void invitePropertyManager(Scanner scanner, User landlord) {
        System.out.println("Invite a Property Manager:");
        System.out.print("Enter Property Address: ");
        String propertyAddress = scanner.nextLine();
        Property property = null;
        for (Property p : properties) {
            if (p.getAddress().equals(propertyAddress)) {
                property = p;
                break;
            }
        }

        if (property == null) {
            System.out.println("Property not found.");
            return;
        }

        System.out.print("Enter Property Manager Email: ");
        String pmEmail = scanner.nextLine();
        User propertyManager = null;
        for (User user : users) {
            if (user.getEmail().equals(pmEmail) && user.getUserType().equals("propertyManager")) {
                propertyManager = user;
                break;
            }
        }

        if (propertyManager == null) {
            System.out.println("Property Manager not found.");
            return;
        }

        // Simulate sending an invitation to the property manager
        System.out.println("Invitation sent to " + propertyManager.getFirstName() + " to manage the property at " + property.getAddress());
    }
}
