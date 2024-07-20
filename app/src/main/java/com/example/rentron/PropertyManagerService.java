package com.example.rentron;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PropertyManagerService {
    private DatabaseReference database;

    public PropertyManagerService() {
        this.database = FirebaseDatabase.getInstance().getReference("PropertyManagers");
    }

    public void assignProperty(String managerId, String propertyId) {
        database.child(managerId).child("assignedProperties").child(propertyId).setValue(true);
    }

    public void viewAssignedProperties(String managerId, PropertyService.PropertyCallback callback) {
        database.child(managerId).child("assignedProperties").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> propertyIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    propertyIds.add(snapshot.getKey());
                }
                PropertyService propertyService = new PropertyService();
                propertyService.getPropertiesByIds(propertyIds, callback);
            }


            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void handleTicket(String managerId, String ticketId, String action, String message) {
        TicketService ticketService = new TicketService();
        if (action.equals("accept")) {
            ticketService.updateTicketStatus(ticketId, "In-progress", "Accepted by manager");
        } else if (action.equals("reject")) {
            ticketService.updateTicketStatus(ticketId, "Rejected", "Rejected by manager: " + message);
        } else if (action.equals("resolve")) {
            ticketService.updateTicketStatus(ticketId, "Resolved", "Resolved by manager: " + message);
        }
    }
}
