package com.example.rentron;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<User> users = new ArrayList<>();
    private static List<Property> properties = new ArrayList<>();
    private static List<RentalRequest> rentalRequests = new ArrayList<>();
    private static List<Ticket> tickets = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Rentron!");

        while (true) {
            System.out.println("Choose an option: ");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser(scanner);
                    break;
                case 2:
                    loginUser(scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter role (Landlord/Client/PropertyManager): ");
        String role = scanner.nextLine();
        int birthYear = 0;
        if (role.equalsIgnoreCase("Client")) {
            System.out.print("Enter birth year: ");
            birthYear = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        }

        User newUser = new User(firstName, lastName, email, password, role, birthYear);
        users.add(newUser);
        System.out.println("User registered successfully.");
    }

    private static void loginUser(Scanner scanner) {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        final User[] userContainer = new User[1];
        users.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .ifPresent(user -> userContainer[0] = user);

        if (userContainer[0] != null) {
            System.out.println("User logged in successfully: " + userContainer[0].getEmail());
            displayUserMenu(userContainer[0], scanner);
        } else {
            System.out.println("Login failed: Invalid email or password.");
        }
    }

    private static void displayUserMenu(User user, Scanner scanner) {
        while (true) {
            System.out.println("Choose an option: ");
            if (user.getRole().equalsIgnoreCase("Landlord")) {
                System.out.println("1. Add Property");
                System.out.println("2. View Properties");
                System.out.println("3. Assign Manager");
                System.out.println("4. Log Out");
            } else if (user.getRole().equalsIgnoreCase("Client")) {
                System.out.println("1. Search Properties");
                System.out.println("2. Book Property");
                System.out.println("3. View Booked Properties");
                System.out.println("4. Submit Rental Request");
                System.out.println("5. View Rental Requests");
                System.out.println("6. Log Out");
            } else if (user.getRole().equalsIgnoreCase("PropertyManager")) {
                System.out.println("1. View Assigned Properties");
                System.out.println("2. Handle Tickets");
                System.out.println("3. Log Out");
            }
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    if (user.getRole().equalsIgnoreCase("Landlord")) {
                        addProperty(scanner, user.getId());
                    } else if (user.getRole().equalsIgnoreCase("Client")) {
                        searchProperties(scanner);
                    } else if (user.getRole().equalsIgnoreCase("PropertyManager")) {
                        viewAssignedProperties(user.getId());
                    }
                    break;
                case 2:
                    if (user.getRole().equalsIgnoreCase("Landlord")) {
                        viewProperties(user.getId());
                    } else if (user.getRole().equalsIgnoreCase("Client")) {
                        bookProperty(scanner, user.getId());
                    } else if (user.getRole().equalsIgnoreCase("PropertyManager")) {
                        handleTickets(scanner, user.getId());
                    }
                    break;
                case 3:
                    if (user.getRole().equalsIgnoreCase("Landlord")) {
                        assignManager(scanner, user.getId());
                    } else if (user.getRole().equalsIgnoreCase("Client")) {
                        viewBookedProperties(user.getId());
                    } else if (user.getRole().equalsIgnoreCase("PropertyManager")) {
                        System.out.println("Logging out...");
                        return;
                    }
                    break;
                case 4:
                    if (user.getRole().equalsIgnoreCase("Landlord") || user.getRole().equalsIgnoreCase("Client")) {
                        System.out.println("Logging out...");
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addProperty(Scanner scanner, String userId) {
        System.out.print("Enter property address: ");
        String address = scanner.nextLine();
        System.out.print("Enter property type: ");
        String type = scanner.nextLine();
        System.out.print("Enter number of rooms: ");
        int rooms = scanner.nextInt();
        System.out.print("Enter number of bathrooms: ");
        int bathrooms = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Property newProperty = new Property(null, address, type, 0, rooms, bathrooms, 0, 0, false, 0, 0, new boolean[]{false, false, false}, userId);
        properties.add(newProperty);
        System.out.println("Property added successfully.");
    }

    private static void viewProperties(String userId) {
        properties.stream()
                .filter(property -> property.getLandlordId().equals(userId))
                .forEach(property -> System.out.println("Property: " + property.getAddress()));
    }

    private static void assignManager(Scanner scanner, String userId) {
        System.out.print("Enter property ID: ");
        String propertyId = scanner.nextLine();
        System.out.print("Enter manager ID: ");
        String managerId = scanner.nextLine();
        System.out.print("Enter commission: ");
        double commission = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        properties.stream()
                .filter(property -> property.getId().equals(propertyId) && property.getLandlordId().equals(userId))
                .findFirst()
                .ifPresent(property -> {
                    property.setManagerId(managerId);
                    System.out.println("Manager assigned successfully.");
                });
    }

    private static void searchProperties(Scanner scanner) {
        System.out.print("Enter property type: ");
        String type = scanner.nextLine();
        System.out.print("Enter minimum number of rooms: ");
        int minRooms = scanner.nextInt();
        System.out.print("Enter minimum number of bathrooms: ");
        int minBathrooms = scanner.nextInt();
        System.out.print("Enter minimum number of floors: ");
        int minFloors = scanner.nextInt();
        System.out.print("Enter minimum area: ");
        double minArea = scanner.nextDouble();
        System.out.print("Enter minimum rent: ");
        double minRent = scanner.nextDouble();
        System.out.print("Enter maximum rent: ");
        double maxRent = scanner.nextDouble();
        System.out.print("Enter minimum number of parking spots: ");
        int minParkingSpots = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean[] utilitiesIncluded = new boolean[3];
        System.out.print("Include Hydro (true/false): ");
        utilitiesIncluded[0] = scanner.nextBoolean();
        System.out.print("Include Heating (true/false): ");
        utilitiesIncluded[1] = scanner.nextBoolean();
        System.out.print("Include Water (true/false): ");
        utilitiesIncluded[2] = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline

        SearchCriteria criteria = new SearchCriteria(type, minRooms, minBathrooms, minFloors, minArea, minRent, maxRent, utilitiesIncluded, minParkingSpots);
        properties.stream()
                .filter(criteria::matches)
                .forEach(property -> System.out.println("Found property: " + property.getAddress()));
    }

    private static void bookProperty(Scanner scanner, String clientId) {
        System.out.print("Enter property ID: ");
        String propertyId = scanner.nextLine();
        properties.stream()
                .filter(property -> property.getId().equals(propertyId) && !property.isOccupied())
                .findFirst()
                .ifPresent(property -> {
                    property.setOccupied(true);
                    property.setClientId(clientId);
                    System.out.println("Property booked successfully.");
                });
    }

    private static void viewBookedProperties(String clientId) {
        properties.stream()
                .filter(property -> property.getClientId() != null && property.getClientId().equals(clientId))
                .forEach(property -> System.out.println("Booked property: " + property.getAddress()));
    }

    private static void viewAssignedProperties(String managerId) {
        properties.stream()
                .filter(property -> property.getManagerId() != null && property.getManagerId().equals(managerId))
                .forEach(property -> System.out.println("Assigned property: " + property.getAddress()));
    }

    private static void handleTickets(Scanner scanner, String managerId) {
        System.out.print("Enter ticket ID: ");
        String ticketId = scanner.nextLine();
        System.out.print("Enter action (accept/reject/resolve): ");
        String action = scanner.nextLine();
        String message;
        if (action.equals("reject") || action.equals("resolve")) {
            System.out.print("Enter message: ");
            message = scanner.nextLine();
        } else {
            message = null;
        }
        tickets.stream()
                .filter(ticket -> ticket.getId().equals(ticketId) && ticket.getManagerId().equals(managerId))
                .findFirst()
                .ifPresent(ticket -> {
                    ticket.setStatus(action);
                    if (message != null) {
                        ticket.getHistory().add(message);
                    }
                    System.out.println("Ticket " + action + "ed successfully.");
                });
    }
}
