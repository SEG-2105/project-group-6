package com.example.rentron;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TicketService {
    private DatabaseReference database;

    public TicketService() {
        this.database = FirebaseDatabase.getInstance().getReference("Tickets");
    }

    public void createTicket(Ticket ticket) {
        if (!ValidationUtils.isValidTicket(ticket)) {
            System.out.println("Invalid ticket details.");
            return;
        }

        String ticketId = database.push().getKey();
        ticket.setId(ticketId);
        database.child(ticketId).setValue(ticket).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Ticket created successfully.");
            } else {
                System.out.println("Failed to create ticket.");
            }
        });
    }

    public void updateTicketStatus(String ticketId, String status, String message) {
        database.child(ticketId).child("status").setValue(status);
        database.child(ticketId).child("history").push().setValue(message + " at " + new Date());
    }

    public void addRatingToTicket(String ticketId, int rating, String explanation) {
        database.child(ticketId).child("rating").setValue(rating);
        database.child(ticketId).child("ratingExplanation").setValue(explanation);
        database.child(ticketId).child("history").push().setValue("Rated at " + new Date() + ": " + rating + "/5 - " + explanation);
    }

    public void getTicketsByPropertyId(String propertyId, TicketCallback callback) {
        database.orderByChild("propertyId").equalTo(propertyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ticket> tickets = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    tickets.add(ticket);
                }
                callback.onSuccess(tickets);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void getTicketsByManagerId(String managerId, TicketCallback callback) {
        database.orderByChild("managerId").equalTo(managerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ticket> tickets = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Ticket ticket = snapshot.getValue(Ticket.class);
                    tickets.add(ticket);
                }
                callback.onSuccess(tickets);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void getTicketsByIds(List<String> ticketIds, TicketCallback callback) {
        List<Ticket> tickets = new ArrayList<>();
        for (String ticketId : ticketIds) {
            database.child(ticketId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    tickets.add(ticket);
                    if (tickets.size() == ticketIds.size()) {
                        callback.onSuccess(tickets);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onFailure(databaseError.getMessage());
                }
            });
        }
    }

    public interface TicketCallback {
        void onSuccess(List<Ticket> tickets);
        void onFailure(String message);
    }
}
