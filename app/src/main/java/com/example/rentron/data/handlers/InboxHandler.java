package com.example.rentron.data.handlers;

import android.util.Log;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.TicketEntityModel;
import com.example.rentron.data.models.inbox.Ticket;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.screens.PropertyManagerScreen;
import com.example.rentron.data.models.inbox.PropertyManagerInbox;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Response;
import com.example.rentron.utils.Result;

import java.util.List;

/**
 * Class to handle operations related to PropertyManager's Inbox
 */
public class InboxHandler {

    private StatefulView uiScreen;
    private PropertyManagerScreen propertyManagerScreen;

    private enum dbOperations {
        ADD_TICKET
    }

    /**
     * Checks if current user has access to property manager only resources
     * @return Response object indicating success or failure (with error message if any)
     */
    private Response userHasAccess() {
        if (App.userIsPropertyManager()) {
            return new Response(true);
        } else {
            return new Response(false, "Access denied. User is not a property manager.");
        }
    }

    /**
     * Get App's property manager inbox if current user has access
     * @return Result object - if operation successful, contains PropertyManagerInbox, else String containing error message
     */
    public Result<PropertyManagerInbox, String> getPropertyManagerInbox() {
        // check if current user does not have access (user is not a Property Manager)
        if (userHasAccess().isError()) {
            return new Result<>(null, userHasAccess().getErrorMessage());
        }
        // attempt to get Property Manager's inbox
        try {
            return new Result<>(App.getPropertyManagerInbox(), null);
        } catch (IllegalAccessException e) {
            return new Result<>(null, "Could not retrieve property manager inbox. Access denied.");
        }
    }

    /**
     * Method to update App's PropertyManagerInbox
     * Retrieves all tickets from the database and creates a new PropertyManagerInbox
     * Stores the inbox by setting AppInstance's propertyManagerInbox to this new inbox
     * @return Response object indicating success or failure
     */
    public Response updatePropertyManagerInbox(PropertyManagerScreen inboxView) {
        // check if current user does not have access (user is not a property manager)
        if (userHasAccess().isError()) {
            return userHasAccess();
        }

        // set inbox view
        this.propertyManagerScreen = inboxView;

        // make async call to fetch all tickets from database
        App.getPrimaryDatabase().INBOX.getAllTickets(this);

        // return response once request to get all tickets has been submitted
        return new Response(true, "retrieving tickets for property manager");
    }

    /**
     * Handles the response from async call made to obtain all tickets from database in updatePropertyManagerInbox method
     * Creates PropertyManagerInbox using the list of tickets and updates App's propertyManagerInbox
     * @param tickets list of tickets retrieved from database
     */
    public void createNewPropertyManagerInbox(List<Ticket> tickets){
        Log.e("ticket", "called 3");
        // validate tickets
        if (Preconditions.isNotEmptyList(tickets)) {
            // instantiate a new property manager inbox by providing it list of tickets
            // set App's new property manager inbox
            try {
                App.setPropertyManagerInbox(new PropertyManagerInbox(tickets));
            } catch (NullPointerException e) {
                Log.e("createNewPropertyManagerInbox", "one of the tickets is null: " + e.getMessage());

                // guard-clause - make sure we have a valid instance of property manager screen
                if (propertyManagerScreen == null) {
                    Log.e("createNewPropertyManagerInbox", "propertyManagerScreen has not been instantiated yet, is null");
                } else {
                    propertyManagerScreen.dbOperationFailureHandler(PropertyManagerScreen.dbOperations.GET_TICKETS, "Failed to load tickets");
                }

            } catch (Exception e) {
                Log.e("createNewPropertyManagerInbox", "an exception occurred while creating Property Manager Inbox: " + e.getMessage());
                // guard-clause - make sure we have a valid instance of property manager screen
                if (propertyManagerScreen == null) {
                    Log.e("createNewPropertyManagerInbox", "propertyManagerScreen has not been instantiated yet, is null");
                } else {
                    propertyManagerScreen.dbOperationFailureHandler(PropertyManagerScreen.dbOperations.GET_TICKETS, "Failed to load tickets");
                }
            }

            // call method in inboxView to update inbox so property manager can see all tickets
            propertyManagerScreen.dbOperationSuccessHandler(PropertyManagerScreen.dbOperations.GET_TICKETS, "Tickets loaded!");
        } else {
            // display error in inbox view
            propertyManagerScreen.dbOperationFailureHandler(PropertyManagerScreen.dbOperations.GET_TICKETS, "No tickets available for property manager inbox");
        }
    }

    /**
     * Handle any error if it happens during retrieval of tickets from database
     * @param message error message
     */
    public void errorGettingTickets(String message) {
        // display error on inbox view
        Log.d("errorGettingTickets", message );
    }

    /**
     * Add new ticket to the App's property manager inbox and 'Ticket' collection on database
     * @param ticketEntityModel unvalidated ticket data
     * @return Response indicating error, if current user doesn't have access
     */
    public Response addNewTicket(TicketEntityModel ticketEntityModel, StatefulView uiScreen) {
        // check if user has access
        if (userHasAccess().isError()) {
            return userHasAccess();
        }

        Ticket ticket = null;
        // set UI Screen so error and success can be notified
        this.uiScreen = uiScreen;

        try {
            // try to create a Ticket object using unvalidated ticket data
            ticket = new Ticket(ticketEntityModel);
            // if data validation successful, initiate process to add ticket to database
            // once ticket has been added to database, it will be added locally to PropertyManagerInbox by successAddingTicket
            App.getPrimaryDatabase().INBOX.addTicket(ticket, this);
        } catch (Exception e) {
            errorAddingTicket(e.getMessage());
        }

        // make the async call to add ticket to database

        return new Response(false, "method not implemented yet");
    }

    /**
     * Method to let UI know async operation to add ticket to database completed
     */
    public void successAddingTicket(Ticket ticket) {
        // once ticket has been added to database (it will have an id)
        try {
            if (App.getAppInstance().userIsPropertyManager()) {
                App.getPropertyManagerInbox().addTicket(ticket);
                // let ui know adding ticket has completed
                // inboxView.successAddingTicket();
            } else {
                // let ui know of success
                this.uiScreen.dbOperationSuccessHandler(dbOperations.ADD_TICKET, "Ticket added successfully");
            }
        } catch (Exception e) {
            errorAddingTicket("Ticket added on database, but failed to add to PropertyManagerInbox" + e.getMessage());
        }
    }

    /**
     * Handle any error if it happens during adding ticket to the database
     * @param message error message
     */
    public void errorAddingTicket(String message) {
        // display error on inbox view
        Log.d("errorAddingTicket", message );
        if (uiScreen != null) {
            this.uiScreen.dbOperationFailureHandler(dbOperations.ADD_TICKET, message);
        }
    }

    /**
     * Remove a ticket from App's PropertyManagerInbox and 'Ticket' collection on database
     * @param ticketId id of the ticket to be removed
     * @return Response indicating error, if current user doesn't have access
     */
    public Response removeTicket(String ticketId) {
        // check if user has access
        if (userHasAccess().isError()) {
            return userHasAccess();
        }

        try {
            // remove ticket from App's PropertyManagerInbox
            App.getPropertyManagerInbox().removeTicket(ticketId);

            // remove ticket from firebase
            App.getPrimaryDatabase().INBOX.removeTicket(ticketId, this);

        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }

        return new Response(false, "method not implemented yet");
    }

    /**
     * Method to let UI know async operation to remove ticket from database completed
     */
    public void successRemovingTicket() {
        // let ui know adding ticket has completed
        // inboxView.successRemovingTicket();
    }

    /**
     * Handle any error if it happens during adding ticket to the database
     * @param message error message
     */
    public void errorRemovingTicket(String message) {
        // display error on inbox view
        Log.d("errorRemovingTicket", message );
    }
}
