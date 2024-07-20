package com.example.rentron;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PropertyService {
    private DatabaseReference database;

    public PropertyService() {
        this.database = FirebaseDatabase.getInstance().getReference("Properties");
    }

    public void addProperty(Property property) {
        String propertyId = database.push().getKey();
        property.setId(propertyId);
        database.child(propertyId).setValue(property).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("Property added successfully.");
            } else {
                System.out.println("Failed to add property.");
            }
        });
    }

    public void assignManager(String propertyId, String managerId, double commission) {
        database.child(propertyId).child("managerId").setValue(managerId);
        database.child(propertyId).child("commission").setValue(commission);
    }

    public void searchProperties(SearchCriteria criteria, SearchCallback callback) {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Property> properties = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Property property = snapshot.getValue(Property.class);
                    if (criteria.matches(property)) {
                        properties.add(property);
                    }
                }
                callback.onSuccess(properties);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public void bookProperty(String propertyId, String clientId) {
        database.child(propertyId).child("isOccupied").setValue(true);
        database.child(propertyId).child("clientId").setValue(clientId);
    }

    public void getPropertiesByIds(List<String> propertyIds, PropertyCallback callback) {
        List<Property> properties = new ArrayList<>();
        for (String propertyId : propertyIds) {
            database.child(propertyId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Property property = dataSnapshot.getValue(Property.class);
                    properties.add(property);
                    if (properties.size() == propertyIds.size()) {
                        callback.onSuccess(properties);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onFailure(databaseError.getMessage());
                }
            });
        }
    }

    public void getManagers(ManagerCallback callback) {
        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference("PropertyManagers");
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<PropertyManager> managers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PropertyManager manager = snapshot.getValue(PropertyManager.class);
                    managers.add(manager);
                }
                callback.onSuccess(managers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    public interface SearchCallback {
        void onSuccess(List<Property> properties);
        void onFailure(String message);
    }

    public interface PropertyCallback {
        void onSuccess(List<Property> properties);
        void onFailure(String message);
    }

    public interface ManagerCallback {
        void onSuccess(List<PropertyManager> managers);
        void onFailure(String message);
    }
}
