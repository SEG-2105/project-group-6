package com.example.rentron;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LandlordService {
    private DatabaseReference database;

    public LandlordService() {
        this.database = FirebaseDatabase.getInstance().getReference("Landlords");
    }

    public void addProperty(String landlordId, Property property) {
        DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference("Properties");
        String propertyId = propertyRef.push().getKey();
        property.setId(propertyId);
        property.setLandlordId(landlordId);
        propertyRef.child(propertyId).setValue(property).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Property added successfully.");
                database.child(landlordId).child("properties").child(propertyId).setValue(true);
            } else {
                System.out.println("Failed to add property.");
            }
        });
    }

    public void viewProperties(String landlordId, PropertyService.PropertyCallback callback) {
        database.child(landlordId).child("properties").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void assignManager(String propertyId, String managerId, double commission) {
        DatabaseReference propertyRef = FirebaseDatabase.getInstance().getReference("Properties");
        propertyRef.child(propertyId).child("managerId").setValue(managerId);
        propertyRef.child(propertyId).child("commission").setValue(commission);
    }

    public void simulateManagerAssignment(String propertyId, String managerId) {
        assignManager(propertyId, managerId, 5.0); // Example commission rate
        System.out.println("Property manager assigned automatically for simulation.");
    }

    public void simulateClientAssignment(String propertyId, String clientId) {
        PropertyService propertyService = new PropertyService();
        propertyService.bookProperty(clientId, propertyId);
        System.out.println("Client assigned automatically for simulation.");
    }
}
