package com.example.rentron;

import java.util.List;
import java.util.Scanner;

public class TicketManagement {

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
