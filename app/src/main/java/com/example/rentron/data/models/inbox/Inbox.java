package com.example.rentron.data.models.inbox;

/**
 * Generic Inbox Interface to enforce implementation of certain methods
 */
public interface Inbox {
    /**
     * Add a ticket to the inbox
     * @param ticket Ticket object to be added to inbox
     */
    void addTicket(Ticket ticket);

    /**
     * Remove a ticket by ID
     * @param ticketId ID of ticket to be removed
     */
    void removeTicket(String ticketId);
}
