package com.example.rentron;

import java.util.List;
import java.util.Scanner;

public class ClientManagement {

    public static void showClientMenu(Scanner scanner, List<Property> properties, List<RentalRequest> rentalRequests, List<Ticket> tickets, User client) {
        while (true) {
            System.out.println("\nClient Menu:");
            System.out.println("1. View Available Properties\n2. Search Properties\n3. Submit Rental Request\n4. View Requests\n5. Create Ticket\n6. View Tickets\n7. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    viewAvailableProperties(properties);
                    break;
                case 2:
                    searchProperties(scanner, properties);
                    break;
                case 3:
                    submitRentalRequest(scanner, properties, rentalRequests, client);
                    break;
                case 4:
                    viewRequests(rentalRequests, client);
                    break;
                case 5:
                    createTicket(scanner, properties, tickets, client);
                    break;
                case 6:
                    viewTickets(tickets, client);
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void viewAvailableProperties(List<Property> properties) {
        System.out.println("Available Properties:");
        for (Property property : properties) {
            if (!property.getManagerEmail().isEmpty() && !property.isOccupied()) {
                System.out.println("Address: " + property.getAddress());
                System.out.println("Type: " + property.getType());
                System.out.println("Floor: " + property.getFloor());
                System.out.println("Rooms: " + property.getNumRooms());
                System.out.println("Bathrooms: " + property.getNumBathrooms());
                System.out.println("Floors: " + property.getNumFloors());
                System.out.println("Total Area: " + property.getTotalArea());
                System.out.println("Laundry In-Unit: " + property.isLaundryInUnit());
                System.out.println("Parking Spots: " + property.getParkingSpots());
                System.out.println("Total Rent: " + property.getTotalRent());
                System.out.println("Utilities Hydro: " + property.isUtilitiesHydro());
                System.out.println("Utilities Heating: " + property.isUtilitiesHeating());
                System.out.println("Utilities Water: " + property.isUtilitiesWater());
                System.out.println("Manager Email: " + property.getManagerEmail());
                System.out.println("---------------");
            }
        }
    }

    public static void searchProperties(Scanner scanner, List<Property> properties) {
        System.out.print("Type (optional): ");
        String type = scanner.nextLine();
        System.out.print("Min number of rooms (optional): ");
        int minRooms = scanner.hasNextInt() ? scanner.nextInt() : 0;
        scanner.nextLine(); // Consume newline
        System.out.print("Min number of bathrooms (optional): ");
        int minBathrooms = scanner.hasNextInt() ? scanner.nextInt() : 0;
        scanner.nextLine(); // Consume newline
        System.out.print("Min area (optional): ");
        int minArea = scanner.hasNextInt() ? scanner.nextInt() : 0;
        scanner.nextLine(); // Consume newline
        System.out.print("Min/Max rent price (optional): ");
        double minRent = scanner.hasNextDouble() ? scanner.nextDouble() : 0.0;
        double maxRent = scanner.hasNextDouble() ? scanner.nextDouble() : Double.MAX_VALUE;
        scanner.nextLine(); // Consume newline

        for (Property property : properties) {
            if (!property.getManagerEmail().isEmpty() && !property.isOccupied() &&
                    (type.isEmpty() || property.getType().equalsIgnoreCase(type)) &&
                    property.getNumRooms() >= minRooms &&
                    property.getNumBathrooms() >= minBathrooms &&
                    property.getTotalArea() >= minArea &&
                    property.getTotalRent() >= minRent && property.getTotalRent() <= maxRent) {
                System.out.println(property.getAddress() + " - " + property.getType());
            }
        }
    }

    public static void submitRentalRequest(Scanner scanner, List<Property> properties, List<RentalRequest> rentalRequests, User client) {
        System.out.print("Enter Property Address to Rent: ");
        String propertyAddress = scanner.nextLine();
        Property property = null;

        for (Property p : properties) {
            if (p.getAddress().equalsIgnoreCase(propertyAddress) && !p.isOccupied() && !p.getManagerEmail().isEmpty()) {
                property = p;
                break;
            }
        }

        if (property != null) {
            RentalRequest request = new RentalRequest(client.getEmail(), propertyAddress);
            rentalRequests.add(request);
            System.out.println("Rental request submitted for property at " + property.getAddress());
        } else {
            System.out.println("No available property found with the given address.");
        }
    }

    public static void viewRequests(List<RentalRequest> rentalRequests, User client) {
        System.out.println("Active Requests:");
        for (RentalRequest request : rentalRequests) {
            if (request.getClientEmail().equals(client.getEmail()) && !request.isResolved()) {
                System.out.println("Property: " + request.getPropertyAddress() + " - Status: Active");
            }
        }

        System.out.println("Rejected Requests:");
        for (RentalRequest request : rentalRequests) {
            if (request.getClientEmail().equals(client.getEmail()) && request.isRejected()) {
                System.out.println("Property: " + request.getPropertyAddress() + " - Status: Rejected");
            }
        }
    }

    public static void createTicket(Scanner scanner, List<Property> properties, List<Ticket> tickets, User client) {
        System.out.print("Property Address: ");
        String propertyAddress = scanner.nextLine();
        Property property = null;

        for (Property p : properties) {
            if (p.getAddress().equalsIgnoreCase(propertyAddress) && p.isOccupied()) {
                property = p;
                break;
            }
        }

        if (property != null) {
            System.out.print("Ticket Type (Maintenance, Security, Damage, Infestation): ");
            String type = scanner.nextLine();
            System.out.print("Message: ");
            String message = scanner.nextLine();
            System.out.print("Urgency (1-5): ");
            int urgency = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Ticket ticket = new Ticket(propertyAddress, type, message, urgency, client.getEmail());
            tickets.add(ticket);
            System.out.println("Ticket created successfully!");
        } else {
            System.out.println("No occupied property found with the given address.");
        }
    }

    public static void viewTickets(List<Ticket> tickets, User client) {
        System.out.println("Active Tickets:");
        for (Ticket ticket : tickets) {
            if (ticket.getClientEmail().equals(client.getEmail()) && !ticket.isResolved()) {
                System.out.println("Property: " + ticket.getPropertyAddress() + " - Type: " + ticket.getType() + " - Status: Active");
            }
        }

        System.out.println("Closed Tickets:");
        for (Ticket ticket : tickets) {
            if (ticket.getClientEmail().equals(client.getEmail()) && ticket.isResolved()) {
                System.out.println("Property: " + ticket.getPropertyAddress() + " - Type: " + ticket.getType() + " - Status: Closed");
            }
        }
    }
}
