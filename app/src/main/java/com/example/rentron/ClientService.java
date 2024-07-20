package com.example.rentron;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private DatabaseReference database;

    public ClientService() {
        this.database = FirebaseDatabase.getInstance().getReference("Clients");
    }

    public void bookProperty(String clientId, String propertyId) {
        DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference("Properties");
        propertyRef.child(propertyId).child("isOccupied").setValue(true);
        propertyRef.child(propertyId).child("clientId").setValue(clientId);

        database.child(clientId).child("bookedProperties").child(propertyId).setValue(true);
    }

    public void viewBookedProperties(String clientId, PropertyService.PropertyCallback callback) {
        database.child(clientId).child("bookedProperties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> propertyIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    propertyIds.add(snapshot.getKey());
                }
                PropertyService propertyService = new PropertyService();
                propertyService.getPropertiesByIds(propertyIds, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void submitRentalRequest(String clientId, String propertyId, RentalRequest request) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("RentalRequests");
        String requestId = requestRef.push().getKey();
        request.setId(requestId);
        request.setClientId(clientId);
        request.setPropertyId(propertyId);
        request.setStatus("Pending");
        requestRef.child(requestId).setValue(request);
    }

    public void viewRentalRequests(String clientId, RequestCallback callback) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("RentalRequests");
        requestRef.orderByChild("clientId").equalTo(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RentalRequest> requests = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RentalRequest request = snapshot.getValue(RentalRequest.class);
                    requests.add(request);
                }
                callback.onSuccess(requests);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void cancelRentalRequest(String requestId) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("RentalRequests");
        requestRef.child(requestId).removeValue();
    }

    public void viewTickets(String clientId, TicketService.TicketCallback callback) {
        database.child(clientId).child("tickets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> ticketIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ticketIds.add(snapshot.getKey());
                }
                TicketService ticketService = new TicketService();
                ticketService.getTicketsByIds(ticketIds, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public interface RequestCallback {
        void onSuccess(List<RentalRequest> requests);
        void onFailure(String message);
    }
}
