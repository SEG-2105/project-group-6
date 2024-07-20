package com.example.rentron;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pattern.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static boolean isValidProperty(Property property) {
        return property != null
                && property.getAddress() != null && !property.getAddress().isEmpty()
                && property.getType() != null && !property.getType().isEmpty()
                && property.getRooms() > 0
                && property.getBathrooms() > 0;
    }

    public static boolean isValidTicket(Ticket ticket) {
        return ticket != null
                && ticket.getPropertyId() != null && !ticket.getPropertyId().isEmpty()
                && ticket.getClientId() != null && !ticket.getClientId().isEmpty()
                && ticket.getType() != null && !ticket.getType().isEmpty()
                && ticket.getMessage() != null && !ticket.getMessage().isEmpty()
                && ticket.getUrgency() > 0 && ticket.getUrgency() <= 5;
    }

    public static boolean isValidRentalRequest(RentalRequest request) {
        return request != null
                && request.getClientId() != null && !request.getClientId().isEmpty()
                && request.getPropertyId() != null && !request.getPropertyId().isEmpty();
    }
}
