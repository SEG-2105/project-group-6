package com.example.rentron;

import java.util.List;
import java.util.Scanner;

public class RentalManagement {

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
}
