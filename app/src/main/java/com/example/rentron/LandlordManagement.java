package com.example.rentron;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LandlordManagement {

    public static void showLandlordMenu(Scanner scanner, List<Property> properties, List<User> users, User landlord) {
        while (true) {
            System.out.println("\nLandlord Menu:");
            System.out.println("1. Add Property\n2. View Properties\n3. Edit Property Rent\n4. Invite Property Manager\n5. Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (option) {
                case 1:
                    addProperty(scanner, properties, landlord);
                    break;
                case 2:
                    viewProperties(properties, landlord);
                    break;
                case 3:
                    editPropertyRent(scanner, properties, landlord);
                    break;
                case 4:
                    invitePropertyManager(scanner, properties, users, landlord);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public static void addProperty(Scanner scanner, List<Property> properties, User landlord) {
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

        Property property = new Property(address, type, floor, numRooms, numBathrooms, numFloors, totalArea, laundryInUnit, parkingSpots, totalRent, utilitiesHydro, utilitiesHeating, utilitiesWater, "", landlord.getEmail());
        properties.add(property);
        System.out.println("Property added successfully!");
    }

    public static void viewProperties(List<Property> properties, User landlord) {
        System.out.println("Your Properties:");
        for (Property property : properties) {
            if (property.getLandlordEmail().equals(landlord.getEmail())) {
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
                System.out.println("Occupied: " + property.isOccupied());
                System.out.println("Manager Email: " + property.getManagerEmail());
                System.out.println("---------------");
            }
        }
    }

    public static void editPropertyRent(Scanner scanner, List<Property> properties, User landlord) {
        System.out.println("Select the property to edit the rent:");
        int index = 1;
        for (Property property : properties) {
            if (property.getLandlordEmail().equals(landlord.getEmail())) {
                System.out.println(index + ". " + property.getAddress() + " - Current Rent: " + property.getTotalRent());
                index++;
            }
        }
        System.out.print("Enter the property number: ");
        int propertyIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline

        Property property = properties.get(propertyIndex);
        if (property.getLandlordEmail().equals(landlord.getEmail())) {
            System.out.print("Enter new rent amount: ");
            double newRent = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
            property.setTotalRent(newRent);
            System.out.println("Rent updated successfully!");
        } else {
            System.out.println("Invalid property selection.");
        }
    }

    public static void invitePropertyManager(Scanner scanner, List<Property> properties, List<User> users, User landlord) {
        System.out.println("Invite a Property Manager:");
        System.out.print("Enter Property Address: ");
        String propertyAddress = scanner.nextLine();
        Property property = null;
        for (Property p : properties) {
            if (p.getAddress().equals(propertyAddress) && p.getLandlordEmail().equals(landlord.getEmail())) {
                property = p;
                break;
            }
        }

        if (property == null) {
            System.out.println("Property not found.");
            return;
        }

        List<User> availableManagers = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals("propertyManager")) {
                availableManagers.add(user);
            }
        }

        if (availableManagers.isEmpty()) {
            System.out.println("No property managers available.");
            return;
        }

        System.out.println("Available Property Managers:");
        for (int i = 0; i < availableManagers.size(); i++) {
            User manager = availableManagers.get(i);
            System.out.println((i + 1) + ". " + manager.getFirstName() + " " + manager.getLastName() + " (" + manager.getEmail() + ")");
        }

        System.out.print("Choose a property manager (enter number): ");
        int managerIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline

        if (managerIndex < 0 || managerIndex >= availableManagers.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        User chosenManager = availableManagers.get(managerIndex);
        property.setManagerEmail(chosenManager.getEmail());
        System.out.println("Invitation sent to " + chosenManager.getFirstName() + " to manage the property at " + property.getAddress());
    }
}
