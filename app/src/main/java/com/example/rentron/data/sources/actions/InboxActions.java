package com.example.rentron.data.sources.actions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rentron.data.handlers.InboxHandler;
import com.example.rentron.data.models.inbox.Ticket;
import com.example.rentron.data.sources.FirebaseRepository;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InboxActions {
    FirebaseFirestore database;
    FirebaseRepository firebaseRepository;
    final static private String TICKETS_COLLECTION = "Tickets";

    public InboxActions(FirebaseFirestore database, FirebaseRepository firebaseRepository) {
        this.database = database;
        this.firebaseRepository = firebaseRepository;
    }

    /**
     * Get all tickets from Firebase
     * @param inboxHandler reference to instance of inbox handler to pass operation response
     */
    public void getAllTickets(InboxHandler inboxHandler) {
        Log.e("ticket", "called 1");
        // get all tickets from Firestore and once done, call appropriate method in inboxHandler
        database.collection(TICKETS_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Ticket> tickets = new ArrayList<>();
                    Log.e("ticket", "called 2");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            tickets.add(getTicketObject(document.getId(), document.getData()));
                        } catch (Exception e) {
                            inboxHandler.errorGettingTickets("Failed to get tickets!");
                            Log.e("getAllTickets", "Failed to get tickets. Error: " + e.getMessage());
                        }
                    }
                    // pass tickets to inbox handler
                    inboxHandler.createNewPropertyManagerInbox(tickets);
                } else {
                    inboxHandler.errorGettingTickets("Error getting tickets from database: " + task.getException());
                }
            }
        });
    }

    /**
     * Uses the provided data and returns a new Ticket instance
     * @param ticketId id of ticket
     * @param data Map containing all other required ticket data with the required property (key) names
     * @return a new Ticket object with associated data
     * @throws ParseException throws ParseException if creation of Ticket fails due to invalid dateSubmitted format
     */
    private Ticket getTicketObject(String ticketId, Map<String, Object> data) throws ParseException {
        // cast object values in data to string
        Map<String, String> ticketData = Utilities.convertMapValuesToString(data);
        // return ticket object
        return new Ticket(
                ticketId,
                ticketData.get(Ticket.TICKET_PROPERTY.title.toString()),
                ticketData.get(Ticket.TICKET_PROPERTY.description.toString()),
                ticketData.get(Ticket.TICKET_PROPERTY.clientId.toString()),
                ticketData.get(Ticket.TICKET_PROPERTY.landlordId.toString()),
                ticketData.get(Ticket.TICKET_PROPERTY.dateSubmitted.toString())
        );
    }

    /**
     * Add a ticket to firebase
     * @param ticket Ticket object of ticket to be added
     * @param inboxHandler reference to instance of inbox handler to pass operation response
     */
    public void addTicket(Ticket ticket, InboxHandler inboxHandler) {
        // proceed only if preconditions satisfied
        if (Preconditions.isNotNull(ticket) && Preconditions.isNotNull(inboxHandler)) {
            Map<String, Object> ticketData = ticket.getTicketDataMap();

            Log.e("ticket", "adding ticket");

            database.collection(TICKETS_COLLECTION)
                    .add(ticketData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // update ticket id
                            ticket.setId(documentReference.getId());
                            inboxHandler.successAddingTicket(ticket);
                            Log.e("ticket", "added ticket");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            inboxHandler.errorAddingTicket("Failed to add ticket to database: " + e.getMessage());
                        }
                    });
        } else {
            // if initial preconditions fail
            // if inbox handler is valid
            if (Preconditions.isNotNull(inboxHandler)) {
                inboxHandler.errorAddingTicket("Invalid ticket object provided");
            }
            Log.e("addTicket", "Invalid object values for ticket and inboxHandler");
        }
    }

    /*public void addSampleTicket(Ticket ticket) {
        Map<String, Object> ticketData = ticket.getTicketDataMap();
        database.collection(TICKETS_COLLECTION)
                .add(ticketData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // update ticket id
                        ticket.setId(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("addSampleTicket", "Failed to add ticket to database: " + e.getMessage());
                    }
                });
    }*/

    /**
     * Remove ticket from Firebase
     * @param ticketId id of ticket to be removed
     * @param inboxHandler reference to instance of inbox handler to pass operation response
     */
    public void removeTicket(String ticketId, InboxHandler inboxHandler) {

        // proceed only if preconditions satisfied
        if (Preconditions.isNotEmptyString(ticketId) && Preconditions.isNotNull(inboxHandler)) {
            database.collection(TICKETS_COLLECTION).document(ticketId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            inboxHandler.successRemovingTicket();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("removeTicket", "Error deleting document", e);
                            inboxHandler.errorRemovingTicket("Error removing document from database: " + e.getMessage());
                        }
                    });
        } else {
            // if initial preconditions fail
            // if inbox handler is valid
            if (Preconditions.isNotNull(inboxHandler)) {
                inboxHandler.errorRemovingTicket("Invalid ticket id provided");
            } else {
                Log.e("removeTicket", "Invalid object values for ticketId and inboxHandler");
            }
        }

    }

}
