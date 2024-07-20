package com.example.rentron.data.sources.actions;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.AddressEntityModel;
import com.example.rentron.data.entity_models.CreditCardEntityModel;
import com.example.rentron.data.entity_models.UserEntityModel;
import com.example.rentron.data.handlers.UserHandler;
import com.example.rentron.data.models.Address;
import com.example.rentron.data.models.PropertyManager;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.CreditCard;
import com.example.rentron.data.models.UserRoles;
import com.example.rentron.ui.screens.TicketScreen;
import com.example.rentron.ui.screens.LoginScreen;
import com.example.rentron.utils.Response;

import static com.example.rentron.data.sources.FirebaseCollections.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Locale;

public class UserActions {

    FirebaseFirestore database;

    public UserActions(FirebaseFirestore database) {
        this.database = database;
    }

    protected void getUserById(String userId, LoginScreen loginScreen) {
        // first check if Property Manager
        // then check if Client
        // then check if Landlord
        DocumentReference userReference = database.collection(PROPERTY_MANAGER_COLLECTION).document(userId);

        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    if (document.getData() != null){
                        Response r = makePropertyManagerFromFirebase(document, userId);
                        if (loginScreen != null && r.isSuccess()) {
                            loginScreen.showNextScreen();
                        } else if (loginScreen != null){
                            Log.e("Login failed for property manager", r.getErrorMessage());
                            loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "Login failed for property manager: " + r.getErrorMessage());
                        }

                        if (r.isError()) {
                            Log.e("getUserById: ", r.getErrorMessage());
                        }
                    }

                } else {
                    // if user not in Property Manager collection, check Landlord
                    getLandlordById(userId, loginScreen);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    protected void getLandlordById(String userId, LoginScreen loginScreen) {
        DocumentReference userReference = database.collection(LANDLORD_COLLECTION).document(userId);

        // get Landlord's data
        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    if (document.getData() != null){

                        // if landlord is suspended, we don't need to store Landlord data locally

                        // get value of isSuspended for check
                        boolean isSuspended = (Boolean) document.getData().get("isSuspended");

                        Response r = makeLandlordFromFirebase(document);

                        // if landlord is suspended, return right away with empty landlord
                        if (isSuspended) {
                            // get suspension date
                            if (document.getData().get("suspensionDate") != null) {
                                String landlordSuspensionDate = String.valueOf((document.getData().get("suspensionDate")));

                                // inform UI that Landlord is suspended and provide it suspension date
                                loginScreen.handleSuspendedLandlordLogin(landlordSuspensionDate, String.valueOf(document.getData().get("firstName")));
                            } else {
                                loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN,"Could not retrieve a valid date for suspended landlord");
                            }
                            // return
                            new Response(false, "Landlord is suspended");
                        }

                        // if Landlord is not suspended, we create the Landlord instance
                        else {
                            if (loginScreen != null && r.isSuccess()) {
                                // load Landlord's properties
                                // app's user should have been set to the Landlord by this point (App.getUser() = logged in landlord)
                                App.getPrimaryDatabase().REQUESTS.loadLandlordRequests(userId);
                                App.getPrimaryDatabase().PROPERTIES.loadLandlordProperties(loginScreen);

                            } else if (loginScreen != null){
                                Log.e("Login failed for landlord", r.getErrorMessage());
                                loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN,"Login failed, " + r.getErrorMessage());
                            }
                        }
                    }

                } else {
                    getClientById(userId, loginScreen);
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

    }

    protected void getClientById(String userId, LoginScreen loginScreen) {
        DocumentReference userReference = database.collection(CLIENT_COLLECTION).document(userId);

        userReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    if (document.getData() != null){
                        Response r = makeClientFromFirebase(document);
                        if (loginScreen != null && r.isSuccess()) {
                            App.getPrimaryDatabase().REQUESTS.loadClientRequests(userId);
                            loginScreen.showNextScreen();
                        } else if (loginScreen != null){
                            Log.e("Login failed for client", r.getErrorMessage());
                            loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN,"Login failed for user: " + r.getErrorMessage());
                        }

                        if (r.isError()) {
                            Log.e("getUserById: ", r.getErrorMessage());
                        }
                    }

                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private Response makePropertyManagerFromFirebase(DocumentSnapshot document, String uid){

        try{
            if (document.getData() == null) {
                throw new NullPointerException("makePropertyManagerFromFirebase: invalid document object");
            }

            String firstName = String.valueOf(document.getData().get("firstName"));
            String lastName = String.valueOf(document.getData().get("lastName"));
            String email = String.valueOf(document.getData().get("email"));

            App.getAppInstance().setUser(new PropertyManager(uid, firstName, lastName, email));

            return new Response(true);
        } catch (Exception e) {
            return new Response(false, "makePropertyManagerFromFirebase: " + e.getMessage());
        }
    }

    private Response makeClientFromFirebase(DocumentSnapshot document){

        try{

            if (document.getData() == null) {
                throw new NullPointerException("makeClientFromFirebase: invalid document object");
            }

            UserEntityModel newUser = new UserEntityModel();
            AddressEntityModel newAddress = new AddressEntityModel();
            CreditCardEntityModel newCreditCard = new CreditCardEntityModel();

            newUser.setFirstName(String.valueOf(document.getData().get("firstName")));
            newUser.setLastName(String.valueOf(document.getData().get("lastName")));
            newUser.setEmail(String.valueOf(document.getData().get("email")));
            newUser.setUserId(document.getId());
            newUser.setRole(UserRoles.CLIENT);

            newAddress.setStreetAddress(String.valueOf(document.getData().get("addressStreet")));
            newAddress.setCity(String.valueOf(document.getData().get("addressCity")));
            newAddress.setCountry(String.valueOf(document.getData().get("country")));
            newAddress.setPostalCode(String.valueOf(document.getData().get("postalCode")));

            newCreditCard.setBrand(String.valueOf(document.getData().get("creditCardBrand")));
            newCreditCard.setName(String.valueOf(document.getData().get("creditCardName")));
            newCreditCard.setNumber(String.valueOf(document.getData().get("creditCardNumber")));
            newCreditCard.setExpiryMonth(Integer.parseInt(document.getData().get("creditCardExpiryMonth").toString()));
            newCreditCard.setExpiryYear(Integer.parseInt(document.getData().get("creditCardExpiryYear").toString()));
            newCreditCard.setCvc(String.valueOf(document.getData().get("creditCardCvc")));

            Address address = new Address(newAddress);
            CreditCard creditCard = new CreditCard(newCreditCard);

            Client newClient = new Client(newUser, address, creditCard);

            App.getAppInstance().setUser(newClient);

            Log.e("clientS", "Set user as: " + App.getUser().getRole() + "id: " + App.getUser().getUserId());

            return new Response(true);
        } catch (Exception e) {
            return new Response(false, "makeClientFromFirebase: " + e.getMessage());
        }
    }

    protected Response makeLandlordFromFirebase(DocumentSnapshot document){

        try {
            if (document.getData() == null) {
                throw new NullPointerException("makeLandlordFromFirebase: invalid document object");
            }
            UserEntityModel newUser = new UserEntityModel();
            AddressEntityModel newAddress = new AddressEntityModel();
            newUser.setFirstName(String.valueOf(document.getData().get("firstName")));
            newUser.setLastName(String.valueOf(document.getData().get("lastName")));
            newUser.setEmail(String.valueOf(document.getData().get("email")));
            newUser.setUserId(document.getId());
            newUser.setRole(UserRoles.LANDLORD);
            newAddress.setStreetAddress(String.valueOf(document.getData().get("addressStreet")));
            newAddress.setCity(String.valueOf(document.getData().get("addressCity")));
            newAddress.setCountry(String.valueOf(document.getData().get("country")));
            newAddress.setPostalCode(String.valueOf(document.getData().get("postalCode")));
            String description = String.valueOf(document.getData().get("description"));
            String voidCheque = String.valueOf(document.getData().get("voidCheque"));
            Address address = new Address(newAddress);
            Landlord newLandlord = new Landlord(newUser, address, description, voidCheque);
            // if landlord has ratings, add ratings
            Object landlordRating = document.getData().get("ratingSum");
            if (landlordRating != null) {
                newLandlord.setLandlordRatingSum(((Number) landlordRating).doubleValue());

                newLandlord.setNumOfRatings(((Number) document.getData().get("numOfRatings")).intValue());
            }
            newLandlord.setIsSuspended((Boolean) document.getData().get("isSuspended"));
            // if suspension date is NOT null (meaning we do have a value)
            newLandlord.setSuspensionDate(
                    document.getData().get("suspensionDate") != null ?
                            DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(String.valueOf((document.getData().get("suspensionDate")))):
                            null);
            App.getAppInstance().setUser(newLandlord);
            return new Response(true);
        } catch (Exception e) {
            return new Response(false, "makeLandlordFromFirebase: " + e.getMessage());
        }
    }

    /**
     * Method changes fields (isSuspended and suspensionDate) of landlord in firebase based on property manager response
     * to ticket, delete properties and tickets of that landlord is suspended indefinitely
     * @param landlordId id of the landlord associated with the ticket
     * @param isSuspended boolean whether landlord is suspended
     * @param suspensionDate end date of suspension
     */

    public void updateLandlordSuspension(String landlordId, boolean isSuspended, String suspensionDate) {

        // Set the "isSuspended" field to ban boolean and the "suspensionDate" field to suspensionDate date
        database.collection(LANDLORD_COLLECTION).document(landlordId)
                .update(
                        "isSuspended", isSuspended,
                        "suspensionDate", suspensionDate)
                .addOnSuccessListener(aVoid -> {

                    if (suspensionDate == "01/01/9999"){ //  landlord banned indefinitely

//                                                    database.collection(PROPERTIES_COLLECTION)  // delete all properties of this landlord
//                                                             .document(landlordId)
//                                                             .collection(LANDLORD_PROPERTIES_COLLECTION)
//                                                            .document(document.getId())
//                                                            .delete()
//                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void aVoid) {
//
//                                                                    Log.d("Success", "Properties successfully deleted for indefinitely banned landlord!");
//                                                                }
//                                                            })
//                                                            .addOnFailureListener(new OnFailureListener() {
//                                                                @Override
//                                                                public void onFailure(@NonNull Exception e) {
//                                                                    Log.w("Error", "Error deleting properties for indefinitely banned landlord", e);
//                                                                }
//                                                            });

                        database.collection("Tickets")  // delete all tickets of this landlord
                                .whereEqualTo("landlordId", landlordId)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            database.collection("Tickets")
                                                    .document(document.getId())
                                                    .delete()
                                                    .addOnSuccessListener(aVoid1 -> Log.d("Success", "Tickets successfully deleted for indefinitely banned landlord!"))
                                                    .addOnFailureListener(e -> Log.w("Error", "Error deleting tickets for indefinitely banned landlord", e));;
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting tickets for landlord with id: " + landlordId, task.getException());
                                    }
                                });


                    }
                    else {  // landlord NOT banned indefinitely so simply set properties to not offered

                        database.collection(PROPERTIES_COLLECTION)
                                .document(landlordId)
                                .collection(LANDLORD_PROPERTIES_COLLECTION)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            database.collection(PROPERTIES_COLLECTION)
                                                    .document(landlordId)
                                                    .collection(LANDLORD_PROPERTIES_COLLECTION)
                                                    .document(document.getId())
                                                    .update("isOffered", false)
                                                    .addOnSuccessListener(aVoid12 -> Log.d("Success", "Properties successfully updated to not offered!"))
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Error", "Error updating properties", e);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    public void getClientAndLandlordNamesByIds(String clientId, String landlordId, TicketScreen ticketScreen) {
        // first get client name
        database
                .collection(CLIENT_COLLECTION)
                .document(clientId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getData() != null){
                                ticketScreen.dbOperationSuccessHandler(
                                        UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES,
                                        document.getData().get("firstName")
                                                + " " +
                                                document.getData().get("lastName")
                                );
                                // inform ticket screen to update ui
                                ticketScreen.updateUI();
                            } else {
                                Log.e("getClientLandlordName", "document data null");
                                ticketScreen.dbOperationFailureHandler(
                                        UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES,"unable to process request");
                            }
                        } else {
                            Log.e("getClientLandlordName", "client not found for id: " + clientId);
                            ticketScreen.dbOperationFailureHandler(
                                    UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES,"unable to process request");
                        }
                    } else {
                        Log.e(TAG, "getClientLandlordName failed with ", task.getException());
                    }
                });

        // first get client name
        database
                .collection(LANDLORD_COLLECTION)
                .document(landlordId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.getData() != null){
                                ticketScreen.dbOperationSuccessHandler(
                                        UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES,
                                        document.getData().get("firstName")
                                                + " " +
                                                document.getData().get("lastName")
                                );
                                ticketScreen.dbOperationSuccessHandler(
                                        UserHandler.dbOperations.GET_LANDLORD_SUSPENSION_DATE,
                                        String.valueOf((document.getData().get("suspensionDate")))
                                );
                                // inform ticket screen to update ui
                                ticketScreen.updateUI();
                            } else {
                                Log.e("getClientLandlordName", "document data null");
                                ticketScreen.dbOperationFailureHandler(
                                        UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES,"unable to process request");
                            }
                        } else {
                            Log.e("getClientLandlordName", "landlord not found for provided id: " + landlordId);
                            ticketScreen.dbOperationFailureHandler(
                                    UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES,"unable to process request");
                        }
                    } else {
                        Log.e(TAG, "getClientLandlordName failed with ", task.getException());
                    }
                });
    }
}
