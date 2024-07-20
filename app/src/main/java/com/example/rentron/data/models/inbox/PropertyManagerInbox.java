package com.example.rentron.data.models.inbox;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.rentron.utils.Preconditions;

/**
 * Tickets inbox of Property Manager
 */
public class PropertyManagerInbox implements Inbox {
    // store tickets in a Map for quickly accessing any ticket by its id without need for traversal
    private HashMap<String, Ticket> tickets;

    /**
     * Create a new property manager inbox by providing a list of tickets
     * @param inboxTickets list of tickets to be added to the inbox
     */
    public PropertyManagerInbox(List<Ticket> inboxTickets) throws NullPointerException {
        tickets = new HashMap<>(inboxTickets.size());
        this.addTickets(inboxTickets);
    }

    /**
     * Method to get all tickets
     * @return a HashMap structure with keys as ticket id and values as Ticket objects
     */
    public HashMap<String, Ticket> getTickets() {
        return this.tickets;
    }

    public ArrayList<Ticket> getListOfTickets() {
        return new ArrayList<>(this.tickets.values());
    }

    /**
     * Add a ticket to the inbox
     * @param ticket Ticket object to be added to inbox
     * @throws NullPointerException if provided ticket ID object is null
     */
    @Override
    public void addTicket(Ticket ticket) throws NullPointerException {

        // validate ticket object
        if (ticket == null) {
            // log for programmer, and exception message for client
            Log.e("addTicket", "addTicket: Ticket object provided is null");
            throw new NullPointerException("Trying to add an invalid ticket!");
        }
        // add ticket
        this.tickets.put(ticket.getId(), ticket);
    }

    /**
     * Add multiple tickets by providing a list of tickets
     * @param tickets list of tickets to be added to the inbox
     * @throws NullPointerException if provided list of tickets is null
     */
    public void addTickets(List<Ticket> tickets) throws NullPointerException {
        // validate ticket object
        if (Preconditions.isNotEmptyList(tickets)) {
            // add all tickets to the property manager inbox
            for (Ticket ticket: tickets) {
                // add ticket, throws NullPointerException if ticket is null
                this.addTicket(ticket);
            }
        } else {
            // log for programmer, and exception message for client
            Log.e("addTickets", "addTickets: List<Ticket> provided is null");
            throw new NullPointerException("No tickets provided to be added to the inbox!");
        }
    }

    /**
     * Remove a ticket by ID
     * @param ticketId ID of ticket to be removed
     * @throws NullPointerException if provided ticket ID object is null
     */
    @Override
    public void removeTicket(String ticketId) throws NullPointerException {
        if (Preconditions.isNotEmptyString(ticketId)) {
            // remove the ticket
            tickets.remove(ticketId);
        } else {
            // log for programmer, and exception message for client
            Log.e("removeTicket", "ticketId provided is null");
            throw new NullPointerException("No ticket ID provided!");
        }
    }

    /**
     * Get a ticket by id
     * @param ticketId ID of the ticket to retrieve
     * @return Returns the ticket object which matched the provided ticket ID else false
     * @throws NullPointerException if provided ticket ID object is null
     */
    public Ticket getTicket(String ticketId) throws NullPointerException {
        if (Preconditions.isNotEmptyString(ticketId)) {
            // return the ticket object if it exists, else null
            return tickets.get(ticketId);
        } else {
            // log for programmer, and exception message for client
            Log.e("getTicket", "ticketId provided is null");
            throw new NullPointerException("No ticket ID provided!");
        }
    }

}
