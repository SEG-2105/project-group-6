package com.example.rentron.data.sources.actions;

import static com.example.rentron.data.sources.FirebaseCollections.LANDLORD_COLLECTION;
import static com.example.rentron.data.sources.FirebaseCollections.LANDLORD_REQUESTS_COLLECTION;
import static com.example.rentron.data.sources.FirebaseCollections.CLIENT_COLLECTION;
import static com.example.rentron.data.sources.FirebaseCollections.CLIENT_REQUESTS_COLLECTION;
import static com.example.rentron.data.sources.FirebaseCollections.REQUEST_COLLECTION;
import static com.example.rentron.data.handlers.RequestHandler.dbOperations.*;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.AddressEntityModel;
import com.example.rentron.data.models.Address;
import com.example.rentron.data.models.Request;
import com.example.rentron.data.models.requests.LandlordInfo;
import com.example.rentron.data.models.requests.ClientInfo;
import com.example.rentron.data.models.requests.PropertyInfo;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestActions {

    FirebaseFirestore database;

    public RequestActions(FirebaseFirestore database) {
        this.database = database;
    }

    /**
     * Adds request to firebase
     *
     * @param request request object to be added
     */
    public void addRequest(Request request) {

        if (Preconditions.isNotNull(request)) {

            Map<String, Object> databaseRequest = new HashMap<>();

            databaseRequest.put("clientInfo", request.getClientInfo());
            databaseRequest.put("landlordInfo", request.getLandlordInfo());

            databaseRequest.put("date", request.getRequestDate());
            databaseRequest.put("isPending", request.getIsPending());
            databaseRequest.put("isRejected", request.getIsRejected());
            databaseRequest.put("isCompleted", request.getIsCompleted());
            databaseRequest.put("properties", request.getProperties());
            databaseRequest.put("isRated", request.isRated());
            databaseRequest.put("rating", request.getRating());
            databaseRequest.put("ticketSubmitted", request.isTicketSubmitted());

            // Add request to Requests Collection
            database
                    .collection(REQUEST_COLLECTION)
                    .add(databaseRequest)
                    .addOnSuccessListener(documentReference -> {

                        // if successful, update requestId
                        request.setRequestID(documentReference.getId());

                        // if successful, add requestId to specific landlord's list of requests
                        database.collection(LANDLORD_COLLECTION)
                                .document(request.getLandlordInfo().getLandlordId())
                                .update(LANDLORD_REQUESTS_COLLECTION, FieldValue.arrayUnion(request.getRequestID()))
                                .addOnSuccessListener(aVoid -> Log.d("LandlordRequestsSuccess", "DocumentSnapshot successfully updated!"))
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("LandlordRequestsError", "Error updating document: " + e.getMessage());
                                    }
                                });

                        // if successful, add requestId to specific client's list of requests
                        database.collection(CLIENT_COLLECTION)
                                .document(request.getClientInfo().getClientId())
                                .update(CLIENT_REQUESTS_COLLECTION, FieldValue.arrayUnion(request.getRequestID()))
                                .addOnSuccessListener(aVoid -> Log.d("ClientRequestsSuccess", "DocumentSnapshot successfully updated!"))
                                .addOnFailureListener(e -> Log.e("ClientRequestsError", "Error updating document: " + e));

                        App.REQUEST_HANDLER.handleActionSuccess(ADD_REQUEST, request);
                    })
                    .addOnFailureListener(e -> App.REQUEST_HANDLER.handleActionFailure(ADD_REQUEST, "Failed to add request to database: " + e.getMessage()));
        } else {
            App.REQUEST_HANDLER.handleActionFailure(ADD_REQUEST, "Invalid request instance provided");
        }
    }


    /**
     * Removes request from firebase
     *
     * @param requestId requestId of object to be removed
     */
    public void removeRequest(String requestId){

        if (Preconditions.isNotNull(requestId)) {

            // retrieve request object from firebase
            DocumentReference docRef = database.collection(REQUEST_COLLECTION).document(requestId);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        // if the request exists, get clientId and landlordId
                        String clientId = (String) document.get("clientId");
                        String landlordId = (String) document.get("landlordId");

                        // delete requestId from specific landlord's list of requests
                        database.collection(LANDLORD_COLLECTION)
                                .document(landlordId)
                                .update(LANDLORD_REQUESTS_COLLECTION, FieldValue.arrayRemove(requestId));

                        // delete requestId from specific client's list of requests
                        database.collection(CLIENT_COLLECTION)
                                .document(clientId)
                                .update(CLIENT_REQUESTS_COLLECTION, FieldValue.arrayRemove(requestId));

                        //finally, delete request from Requests Collection
                        docRef
                                .delete()
                                .addOnSuccessListener(aVoid -> App.REQUEST_HANDLER.handleActionSuccess(REMOVE_REQUEST, requestId))
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        App.REQUEST_HANDLER.handleActionFailure(REMOVE_REQUEST, "Failed to remove request from database: " + e.getMessage());
                                    }
                                });


                    } else {
                        App.REQUEST_HANDLER.handleActionFailure(REMOVE_REQUEST, "No such document");
                    }
                } else {
                    App.REQUEST_HANDLER.handleActionFailure(REMOVE_REQUEST, "get failed with " + task.getException());
                }
            });
        }
    }

    public void getRequestById(String requestId){

        if (Preconditions.isNotNull(requestId)) {

            // retrieve request object from firebase
            database
                    .collection(REQUEST_COLLECTION)
                    .document(requestId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Request request = makeRequestFromFirebase(document);


                                App.REQUEST_HANDLER.handleActionSuccess(GET_REQUEST_BY_ID, request);

                            } else {
                                Log.d("RemoveRequest", "No such document");
                            }
                        } else {
                            Log.d("RemoveRequest", "get failed with ", task.getException());
                        }
                    });
        }

    }

    public void updateRequest(Request request){

        if (Preconditions.isNotNull(request)) {

            Log.e("request2", request.getRequestID());
            Log.e("request2", "p: " + request.getIsPending());

            database.collection(REQUEST_COLLECTION)
                    .document(request.getRequestID())
                    .update("isPending", request.getIsPending(),
                            "isRejected", request.getIsRejected(),
                            "isCompleted", request.getIsCompleted())
                    .addOnSuccessListener(aVoid -> App.REQUEST_HANDLER.handleActionSuccess(UPDATE_REQUEST,request))
                    .addOnFailureListener(e -> App.REQUEST_HANDLER.handleActionFailure(UPDATE_REQUEST,"Error updating request in firebase"));
        }
    }

    public void updateTicketStatus(Request request){
        if (Preconditions.isNotNull(request)) {

            database.collection(REQUEST_COLLECTION)
                    .document(request.getRequestID())
                    .update("ticketSubmitted", request.isTicketSubmitted())
                    .addOnSuccessListener(aVoid -> App.REQUEST_HANDLER.handleActionSuccess(UPDATE_REQUEST,request))
                    .addOnFailureListener(e -> App.REQUEST_HANDLER.handleActionFailure(UPDATE_REQUEST,"Error updating request in firebase"));
        }
    }

    public void loadLandlordRequests(String landlordId){

        if (Preconditions.isNotNull(landlordId)) {

            // retrieve landlord object from firebase
            database.collection(LANDLORD_COLLECTION)
                    .document(landlordId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                // retrieve list of requestIds from landlord
                                ArrayList<String> requestIds = (ArrayList<String>) document.getData().get(LANDLORD_REQUESTS_COLLECTION);

                                // if no requests
                                if (requestIds == null) {
                                    return;
                                }

                                // iterate through list
                                for (String requestId : requestIds) {

                                    // go to requests_collection and retrieve request using the given requestId in the list
                                    database.collection(REQUEST_COLLECTION)
                                            .document(requestId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {

                                                            //make request object from firebase
                                                            Request request = makeRequestFromFirebase(document);

                                                            //update requests of the logged in landlord

                                                            App.getLandlord().REQUESTS.addRequest(request);

                                                        } else {
                                                            Log.d("loadLandlordRequests", "Request not found given requestId stored in landlord requests");
                                                        }
                                                    } else {
                                                        Log.d("loadLandlordRequests", "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d("loadLandlordRequests", "Landlord not found");
                            }
                        } else {
                            Log.d("loadLandlordRequests", "get failed with ", task.getException());
                        }
                    });
        }
    }

    public void loadClientRequests(String clientId){

        if (Preconditions.isNotNull(clientId)) {

            // retrieve client object from firebase
            database.collection(CLIENT_COLLECTION)
                    .document(clientId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                if (document.getData().get(LANDLORD_REQUESTS_COLLECTION) != null) {

                                    // retrieve list of requestIds from client
                                    ArrayList<String> requestIds = (ArrayList<String>) document.getData().get(LANDLORD_REQUESTS_COLLECTION);


                                    // iterate through list
                                    for (String requestId : requestIds) {

                                        // go to requests_collection and retrieve request using the given requestId in the list
                                        database.collection(REQUEST_COLLECTION)
                                                .document(requestId)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {

                                                                //make request object from firebase
                                                                Request request = makeRequestFromFirebase(document);

                                                                //update requests of the logged in client
                                                                App.getClient().REQUESTS.addRequest(request);

                                                            } else {
                                                                Log.d("loadClientRequests", "Request not found given requestId stored in landlord requests");
                                                            }
                                                        } else {
                                                            Log.d("loadClientRequests", "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d("loadClientRequests", "Landlord not found");
                                }
                            }
                        } else {
                            Log.d("loadClientRequests", "get failed with ", task.getException());
                        }
                    });
        }
    }

    public void updateLandlordRating(String requestId, String landlordId, Double newRating){

        if (Preconditions.isNotNull(landlordId)) {

            // retrieve client object from firebase
            database.collection(LANDLORD_COLLECTION)
                    .document(landlordId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                Double ratingSum = ((Number) document.getData().get("ratingSum")).doubleValue();
                                int numOfRatings = ((Number) document.getData().get("numOfRatings")).intValue();
                                numOfRatings = numOfRatings + 1;

                                database.collection(LANDLORD_COLLECTION)
                                        .document(landlordId)
                                        .update("ratingSum", ratingSum + newRating,
                                                "numOfRatings", numOfRatings)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                database.collection(REQUEST_COLLECTION)
                                                        .document(requestId)
                                                        .update("rating", newRating,
                                                                "isRated", true)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                App.REQUEST_HANDLER.handleUpdateLandlordRatingSuccess();

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                App.REQUEST_HANDLER.handleUpdateLandlordRatingFailure("Failed to update landlord's rating!");
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                App.REQUEST_HANDLER.handleUpdateLandlordRatingFailure("Failed to update landlord's rating!");
                                            }
                                        });

                            } else {
                                Log.d("updateLandlordRating", "Landlord not found");
                            }
                        } else {
                            Log.d("updateLandlordRating" , "get failed with ", task.getException());
                        }
                    });

        }

    }

    protected Request makeRequestFromFirebase(DocumentSnapshot document){

        if (document.getData() == null) {
            throw new NullPointerException("makeRequestFromFirebase: invalid document object");
        }

        Request newRequest = new Request();

        newRequest.setRequestID(document.getId());
        newRequest.setIsCompleted((Boolean)document.getData().get("isCompleted"));
        newRequest.setIsPending((Boolean)document.getData().get("isPending"));
        newRequest.setIsRejected((Boolean)document.getData().get("isRejected"));
        newRequest.setIsRated((Boolean)document.getData().get("isRated"));
        newRequest.setRating(((Number)document.getData().get("rating")).doubleValue());
        if (document.getData().get("ticketSubmitted") != null){
            newRequest.setTicketSubmitted((Boolean)document.getData().get("ticketSubmitted"));
        }else{
            newRequest.setTicketSubmitted((Boolean) false);
        }

        Map<String,Object> landlordData = (Map<String, Object>) document.getData().get("landlordInfo");
        Map<String,Object> clientData = (Map<String, Object>) document.getData().get("clientInfo");
        Map<String, String> landlordAddressData = (Map<String, String>) landlordData.get("landlordAddress");

        AddressEntityModel addressEntityModel = new AddressEntityModel();
        addressEntityModel.setStreetAddress(landlordAddressData.get("streetAddress"));
        addressEntityModel.setCity(landlordAddressData.get("city"));
        addressEntityModel.setCountry(landlordAddressData.get("country"));
        addressEntityModel.setPostalCode(landlordAddressData.get("postalCode"));
        Address landlordAddress = new Address(addressEntityModel);

        LandlordInfo landlordInfo = new LandlordInfo((String) landlordData.get("landlordId"), (String) landlordData.get("landlordName"),
                (landlordData.get("landlordDescription") != null ? (String) landlordData.get("landlordDescription") : "no description available"),
                ((Number)landlordData.get("landlordRating")).intValue(), landlordAddress);

        ClientInfo clientInfo = new ClientInfo((String) clientData.get("clientId"), (String) clientData.get("clientName"),
                (String) clientData.get("clientEmail"));

        newRequest.setLandlordInfo(landlordInfo);
        newRequest.setClientInfo(clientInfo);

        try {
            Timestamp timestamp = (Timestamp) document.getData().get("date");
            Date date = timestamp.toDate();
            newRequest.setDate(date);

        } catch (Exception e) {
            newRequest.setDate(Utilities.getTodaysDate());
            Log.e("DATE", "makeRequestFromFirebase: Error parsing date");
        }

        Map<String,Object> propertiesData = (Map<String, Object>) document.getData().get("properties");
        Map<String,PropertyInfo> properties = new HashMap<>();

        for (Map.Entry<String, Object> entry : propertiesData.entrySet()) {
            String key = entry.getKey();
            Map<String,Object> value = (Map<String,Object>) entry.getValue();

            PropertyInfo propertyInfo = new PropertyInfo((String) value.get("name"), ((Number)value.get("price")).doubleValue(), ((Number)value.get("quantity")).intValue());

            properties.put(key, propertyInfo);
        }

        newRequest.setProperties(properties);

        return newRequest;
    }
}
