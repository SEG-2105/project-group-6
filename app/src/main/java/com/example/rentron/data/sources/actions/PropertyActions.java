package com.example.rentron.data.sources.actions;

import static android.content.ContentValues.TAG;
import static com.example.rentron.data.handlers.PropertyHandler.dbOperations.*;
import static com.example.rentron.data.sources.FirebaseCollections.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.AddressEntityModel;
import com.example.rentron.data.entity_models.PropertyEntityModel;
import com.example.rentron.data.handlers.UserHandler;
import com.example.rentron.data.models.Address;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.data.models.requests.LandlordInfo;
import com.example.rentron.ui.screens.search.SearchPropertyItem;
import com.example.rentron.ui.screens.LoginScreen;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Result;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyActions {

    FirebaseFirestore database;

    public PropertyActions(FirebaseFirestore database) {
        this.database = database;
    }

    private void addLandlordProperty(String landlordPropertiesId, Property property, String landlordName, String landlordAddress) {

        Map<String, Object> databaseProperty = new HashMap<>();
        databaseProperty.put("address", property.getAddress());
        databaseProperty.put("landlordID", property.getLandlordID());
        databaseProperty.put("rooms", property.getRooms());
        databaseProperty.put("propertyType", property.getPropertyType());
        databaseProperty.put("bathrooms", property.getBathrooms());
        databaseProperty.put("amenities", property.getAmenities());
        databaseProperty.put("isOffered", property.isOffered());
        databaseProperty.put("price", property.getPrice());
        databaseProperty.put("keywords", property.getSearchPropertyItemKeywords(landlordName, landlordAddress));

        database.collection(PROPERTIES_COLLECTION)
                .document(landlordPropertiesId)
                .collection(LANDLORD_PROPERTIES_COLLECTION)
                .add(databaseProperty)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // update property id
                        property.setPropertyID(documentReference.getId());
                        Log.e("PROPERTY ID", documentReference.getId());
                        App.PROPERTY_HANDLER.handleActionSuccess(ADD_PROPERTY, property);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY, "Failed to add property to landlord in database: " + e.getMessage());
                    }
                });
    }

    /**
     * Add property to list of properties in Firebase
     * @param property The property to be added
     */
    public void addProperty(Property property) {

        if (Preconditions.isNotNull(property)) {
            // confirm a Landlord is logged in and get the landlord
            Landlord landlord;
            try {
                landlord = (Landlord) App.getUser();

                // Add property to landlord's list in firebase
                database.collection(PROPERTIES_COLLECTION)   // top-level properties collection
                        .whereEqualTo(PROPERTIES_COLLECTION_LANDLORD_KEY, landlord.getUserId()) // get properties document of the landlord
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // if landlord currently doesn't have a property
                                    if (task.getResult() == null || task.getResult().isEmpty()) {
                                        Log.e("addProperty", "Landlord not in properties collection, adding to it");
                                        // add the landlord first
                                        Map<String, Object> propertiesCollectionData = new HashMap<>();
                                        propertiesCollectionData.put("landlord", landlord.getUserId());
                                        database.collection(PROPERTIES_COLLECTION)
                                                .add(propertiesCollectionData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        addLandlordProperty(documentReference.getId(), property, landlord.getFirstName() + " " + landlord.getLastName(), landlord.getAddress().toString());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY, "Failed to add property to landlord in database: " + e.getMessage());
                                                    }
                                                });
                                    } else {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            addLandlordProperty(document.getId(), property, landlord.getFirstName() + " " + landlord.getLastName(), landlord.getAddress().toString());
                                        }
                                    }
                                } else {
                                    App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY, "Failed to add property to landlord in database");
                                    Log.d("addProperty", "Error getting landlord's properties: ", task.getException());
                                }
                            }
                        });

            } catch (Exception e) {
                App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY, "Failed to add property to landlord in database: " + e.getMessage());
            }
        }
    }

    /**
     * Remove property from searchable list of properties in Firebase
     * @param propertyId The propertyId of property to be removed
     */
    public void removeProperty(String propertyId){

        Landlord landlord;

        try {
            if (Preconditions.isNotNull(propertyId)) {

                landlord = (Landlord) App.getUser();

                database.collection(PROPERTIES_COLLECTION)   // top-level properties collection
                        .whereEqualTo(PROPERTIES_COLLECTION_LANDLORD_KEY, landlord.getUserId()) // get properties document of the landlord
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        // Remove property from landlord's list in firebase
                                        database.collection(PROPERTIES_COLLECTION)
                                                .document(document.getId())
                                                .collection(LANDLORD_PROPERTIES_COLLECTION)
                                                .document(propertyId)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        App.PROPERTY_HANDLER.handleActionSuccess(REMOVE_PROPERTY, propertyId);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY, "Failed to remove property in landlord's list in Firebase: " + e.getMessage());
                                                    }
                                                });
                                    }
                                } else {
                                    App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY, "Unable to get landlord's properties: " + task.getException());
                                }
                            }
                        });

            } else {
                // if Preconditions fail
                App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY, "Invalid object value for property");
            }
        } catch (Exception e) {
            App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY, "Unable to get Landlord instance: " + e.getMessage());
        }
    }

    /**
     * Set isOffered property to true to a property in a specific landlord's list of properties in Firebase
     * @param propertyId The propertyId of property to be updated
     */
    public void addPropertyToOfferedList(String propertyId){

        if (Preconditions.isNotNull(propertyId)) {
            try {
                // ensure a landlord is logged in & get the landlord instance
                Landlord landlord = (Landlord) App.getUser();
                // Set isOffered to true in landlord's properties in firebase
                database.collection(PROPERTIES_COLLECTION)   // top-level properties collection
                        .whereEqualTo(PROPERTIES_COLLECTION_LANDLORD_KEY, landlord.getUserId()) // get properties document of the landlord
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        database.collection(PROPERTIES_COLLECTION)
                                                .document(document.getId())
                                                .collection(LANDLORD_PROPERTIES_COLLECTION)
                                                .document(propertyId)
                                                .update("isOffered", true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        App.PROPERTY_HANDLER.handleActionSuccess(ADD_PROPERTY_TO_OFFERED_LIST, propertyId);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY_TO_OFFERED_LIST, "Failed to add property to offered list in landlord in database: " + e.getMessage());
                                                    }
                                                });
                                    }
                                } else {
                                    App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY_TO_OFFERED_LIST, "Failed to retrieve landlord's properties");
                                }
                            }
                        });

            } catch (Exception e) {
                App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY_TO_OFFERED_LIST, "Failed to add property to offered list in landlord in database: " + e.getMessage());
            }
        } else {
            // if Preconditions fail
            App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTY_TO_OFFERED_LIST, "Invalid object value for propertyId");
        }
    }

    /**
     * Set isOffered property to false to a property in a specific landlord's list of properties in Firebase
     * @param propertyId The propertyId of property to be updated
     */
    public void removePropertyFromOfferedList(String propertyId){

        if (Preconditions.isNotNull(propertyId)) {
            try {
                // ensure a landlord is logged in & get the landlord instance
                Landlord landlord = (Landlord) App.getUser();
                // Set isOffered to true in landlord's properties in firebase
                database.collection(PROPERTIES_COLLECTION)   // top-level properties collection
                        .whereEqualTo(PROPERTIES_COLLECTION_LANDLORD_KEY, landlord.getUserId()) // get properties document of the landlord
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        database.collection(PROPERTIES_COLLECTION)
                                                .document(document.getId())
                                                .collection(LANDLORD_PROPERTIES_COLLECTION)
                                                .document(propertyId)
                                                .update("isOffered", false)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        App.PROPERTY_HANDLER.handleActionSuccess(REMOVE_PROPERTY_FROM_OFFERED_LIST, propertyId);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY_FROM_OFFERED_LIST, "Failed to remove property from offered list in database: " + e.getMessage());
                                                    }
                                                });
                                    }
                                } else {
                                    App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY_FROM_OFFERED_LIST, "Error getting landlord's properties");
                                }
                            }
                        });

            } catch (Exception e) {
                App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY_FROM_OFFERED_LIST, "Unable to retrieve a Landlord: " + e.getMessage());
            }
        } else {
            // if Preconditions fail
            App.PROPERTY_HANDLER.handleActionFailure(REMOVE_PROPERTY_FROM_OFFERED_LIST, "Invalid object value for propertyID");
        }
    }

    /**
     * Get property from Firebase given the propertyId AND landlordId
     * @param propertyId The propertyId of property
     */
    public void getPropertyById (String propertyId, String landlordId) {

        database.collection(PROPERTIES_COLLECTION + "/" + landlordId + "/" + LANDLORD_PROPERTIES_COLLECTION)
                .document(propertyId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getData() != null) {
                                try  {
                                    Property property = makePropertyFromFirebase(document);
                                    // set the property id
                                    property.setPropertyID(document.getId());
                                    App.PROPERTY_HANDLER.handleActionSuccess(GET_PROPERTY_BY_ID, property);
                                } catch (Exception e) {
                                    App.PROPERTY_HANDLER.handleActionFailure(GET_PROPERTY_BY_ID, "Error making a property from data retrieved");
                                }
                            } else {
                                App.PROPERTY_HANDLER.handleActionFailure(GET_PROPERTY_BY_ID, "Error getting the property for given id");
                            }
                        } else {
                            App.PROPERTY_HANDLER.handleActionFailure(GET_PROPERTY_BY_ID, "Error getting landlord's properties");
                        }
                    }
                });
    }

    /**
     * Set properties list to specific landlord locally using App instance user
     */
    public void getProperties(){
        try {
            Landlord landlord = (Landlord) App.getUser();
            database.collection(PROPERTIES_COLLECTION)   // top-level properties collection
                    .whereEqualTo(PROPERTIES_COLLECTION_LANDLORD_KEY, landlord.getUserId()) // get properties document of the landlord
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    database.collection(PROPERTIES_COLLECTION)
                                            .document(document.getId())
                                            .collection(LANDLORD_PROPERTIES_COLLECTION)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Map<String, Property> properties = new HashMap<String, Property>();
                                                        Property property;
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            property = makePropertyFromFirebase(document);
                                                            // set the property id
                                                            property.setPropertyID(document.getId());
                                                            properties.put(document.getId(), property);
                                                        }
                                                        App.PROPERTY_HANDLER.handleActionSuccess(GET_MENU, properties);
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                        App.PROPERTY_HANDLER.handleActionFailure(GET_MENU, "Failed to retrieve properties from firebase");
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            App.PROPERTY_HANDLER.handleActionFailure(GET_MENU, "Failed to get menu: " + e.getMessage());
        }
    }

    /**
     * Set properties list to specific landlord locally using landlordID
     */
    public void loadLandlordProperties(LoginScreen loginScreen){
        try {
            Landlord landlord = (Landlord) App.getUser();
            database.collection(PROPERTIES_COLLECTION)   // top-level properties collection
                    .whereEqualTo(PROPERTIES_COLLECTION_LANDLORD_KEY, landlord.getUserId()) // get properties document of the landlord
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // check if landlord has any properties currently
                                if (task.getResult() == null || task.getResult().isEmpty()) {
                                    Log.e("loadProperties", "Landlord has no properties currently");
                                    loginScreen.showNextScreen();
                                } else {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Log.e("loadProperties", "creating properties");
                                        database.collection(PROPERTIES_COLLECTION)
                                                .document(document.getId())
                                                .collection(LANDLORD_PROPERTIES_COLLECTION)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, Property> properties = new HashMap<String, Property>();
                                                            Property property;
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                property = makePropertyFromFirebase(document);
                                                                // set the property id
                                                                property.setPropertyID(document.getId());
                                                                properties.put(document.getId(), property);
                                                            }
                                                            // add properties to Landlord
                                                            ((Landlord) App.getUser()).PROPERTIES.setProperties(properties);
                                                            // let login screen show Landlord screen
                                                            loginScreen.showNextScreen();
                                                        } else {
                                                            Log.d("loadProperties", "Error getting documents: ", task.getException());
                                                            loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "Failed to retrieve properties from firebase");
                                                        }
                                                    }
                                                });
                                    }
                                }
                            } else {
                                loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "Failed to load properties");
                                Log.e("loadProperties", "failed to load properties: " + task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "Failed to get Landlord's properties");
            Log.e("loadProperties", "failed to load properties: " + e.getMessage());
        }
    }

    /**
     * get all properties of all landlords
     */
    public void getAllProperties() {

        Log.e("searchProperties", "Initiating request to get all properties");

        // get all documents from properties collection
        database.collection(PROPERTIES_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // for each Properties row (which contains a landlord id and a nested properties collection
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // make the next call to get Landlord data
                                getLandlordForSearchProperties(document.getId(), (String) document.getData().get("landlord"));
                            }
                        } else {
                            App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTIES_TO_SEARCH_LIST, "Failed to retrieve landlord from Properties: " + task.getException());
                        }
                    }
                });
    }

    private void getLandlordForSearchProperties(String propertyDocumentId, String landlordId) {

        database.collection(LANDLORD_COLLECTION)
                .document(landlordId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if (document.getData() != null){
                                    // if landlord is suspended, we don't need to get the properties of this landlord
                                    // get value of isSuspended for check
                                    boolean isSuspended = (Boolean) document.getData().get("isSuspended");
                                    // we load the properties only if landlord is not suspended
                                    if (!isSuspended) {
                                        // create LandlordInfo for the landlord
                                        Result<LandlordInfo, String> result = getLandlordInfoInstance(document);
                                        if (result.isSuccess()) {
                                            // call next method to retrieve properties of the landlord
                                            getLandlordProperties(propertyDocumentId, result.getSuccessObject());
                                        } else {
                                            App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTIES_TO_SEARCH_LIST, result.getErrorObject());
                                        }
                                    }
                                }

                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    private Result<LandlordInfo, String> getLandlordInfoInstance(DocumentSnapshot document) {

        try {
            String landlordName =
                    document.getData().get("firstName") +
                            " "  + document.getData().get("lastName");

            String landlordDesc = document.getData().get("description") != null ?
                    (String) document.getData().get("description") :
                    "no description available";

            double landlordRating = ((Number) document.getData().get("ratingSum")).doubleValue() / ((Number) document.getData().get("numOfRatings")).intValue();

            AddressEntityModel newAddress = new AddressEntityModel();

            newAddress.setStreetAddress(String.valueOf(document.getData().get("addressStreet")));
            newAddress.setCity(String.valueOf(document.getData().get("addressCity")));
            newAddress.setCountry(String.valueOf(document.getData().get("country")));
            newAddress.setPostalCode(String.valueOf(document.getData().get("postalCode")));

            return new Result<>(new LandlordInfo(document.getId(), landlordName, landlordDesc, landlordRating, new Address(newAddress)), null);
        } catch (Exception e) {
            return new Result<>(null, "Failed to create LandlordInfo: " + e.getMessage());
        }
    }

    private void getLandlordProperties(String propertyDocumentId, LandlordInfo landlordInfo) {

        Log.e("searchProperties", "Initiating request to get properties of landlord: " + landlordInfo.getLandlordName());
        Log.e("searchProperties", "path: " + PROPERTIES_COLLECTION + "/" + propertyDocumentId + "/" + LANDLORD_PROPERTIES_COLLECTION);

        database.collection(PROPERTIES_COLLECTION + "/" + propertyDocumentId + "/" + LANDLORD_PROPERTIES_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<SearchPropertyItem> spItems = new ArrayList<>();
                        SearchPropertyItem spItem;
                        Property property;
                        if (task.getResult() == null || task.getResult().isEmpty()) {
                            Log.e("searchProperties", "empty result");
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // check if we have a isOffered property
                            if (document.getData().get("isOffered") != null) {
                                // get the isOffered property's value
                                boolean isOffered = (Boolean) document.getData().get("isOffered");
                                // if only the property if offered
                                if (isOffered) {
                                    // create the property
                                    property = makePropertyFromFirebase(document);
                                    // set the property id
                                    property.setPropertyID(document.getId());
                                    // add keywords to property instance (only need to do this when we need search property functionality i.e., for a client)
                                    property.setKeywords((ArrayList<String>) document.getData().get("keywords"));
                                    // create SearchPropertyItem adding to it the property and landlordInfo
                                    spItem = new SearchPropertyItem(property, landlordInfo);
                                    Log.e("searchProperties", "adding property: " + property.getAddress());
                                    // store current SearchPropertyItem in our list
                                    spItems.add(spItem);
                                }
                            }
                        }
                        Log.e("searchProperties", "updating properties");
                        // pass list containing SearchPropertyItems to handler so our App's search property list can be updated
                        App.PROPERTY_HANDLER.handleActionSuccess(ADD_PROPERTIES_TO_SEARCH_LIST, spItems);
                    } else {
                        Log.d("searchProperties", "Error getting documents: ", task.getException());
                        App.PROPERTY_HANDLER.handleActionFailure(ADD_PROPERTIES_TO_SEARCH_LIST, "Failed to retrieve properties from firebase");
                    }
                });
    }

    protected Property makePropertyFromFirebase(DocumentSnapshot document) {

        if (document.getData() == null) {
            throw new NullPointerException("makeClientFromFirebase: invalid document object");
        }

        PropertyEntityModel newProperty = new PropertyEntityModel();

        newProperty.setPropertyID(document.getId());
        newProperty.setAddress(String.valueOf(document.getData().get("address")));
        newProperty.setLandlordID(String.valueOf(document.getData().get("landlordId")));
        newProperty.setRooms(((Number) document.getData().get("rooms")).intValue());
        newProperty.setPropertyType(String.valueOf(document.getData().get("propertyType")));
        newProperty.setBathrooms(((Number) document.getData().get("bathrooms")).intValue());
        newProperty.setAmenities((ArrayList<String>) (document.getData().get("amenities")));
        newProperty.setOffered((Boolean) document.getData().get("isOffered"));
        newProperty.setPrice((Double) document.getData().get("price"));

        return new Property(newProperty);

    }

}
