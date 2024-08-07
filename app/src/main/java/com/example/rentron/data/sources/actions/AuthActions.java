package com.example.rentron.data.sources.actions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rentron.app.App;
import com.example.rentron.data.handlers.UserHandler;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.sources.FirebaseRepository;
import com.example.rentron.ui.screens.LoginScreen;
import com.example.rentron.ui.screens.SignupScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthActions {
    final private FirebaseAuth mAuth;
    FirebaseFirestore database;
    FirebaseRepository firebaseRepository;

    public AuthActions(FirebaseAuth mAuth, FirebaseFirestore database, FirebaseRepository firebaseRepository) {
        this.mAuth = mAuth;
        this.database = database;
        this.firebaseRepository = firebaseRepository;
    }

    public void registerClient(String email, String password, SignupScreen signupScreen, Client newUser) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                newUser.setUserId(user.getUid());
                                App.getAppInstance().setUser(newUser);

                                // therefore, add the user to database
                                database = FirebaseFirestore.getInstance();

                                Map<String, Object> databaseUser = new HashMap<>();
                                databaseUser.put("firstName", newUser.getFirstName());
                                databaseUser.put("lastName", newUser.getLastName());
                                databaseUser.put("email", newUser.getEmail());
                                databaseUser.put("addressStreet", newUser.getAddress().getStreetAddress());
                                databaseUser.put("addressCity", newUser.getAddress().getCity());
                                databaseUser.put("country", newUser.getAddress().getCountry());
                                databaseUser.put("postalCode", newUser.getAddress().getPostalCode());
                                databaseUser.put("creditCardBrand", newUser.getClientCreditCard().getBrand());
                                databaseUser.put("creditCardName", newUser.getClientCreditCard().getName());
                                databaseUser.put("creditCardNumber", newUser.getClientCreditCard().getNumber());
                                databaseUser.put("creditCardExpiryMonth", newUser.getClientCreditCard().getExpiryMonth());
                                databaseUser.put("creditCardExpiryYear", newUser.getClientCreditCard().getExpiryYear());
                                databaseUser.put("creditCardCvc", newUser.getClientCreditCard().getCvc());
                                databaseUser.put("requests", null);


                                database.collection("Clients").document(newUser.getUserId())
                                        .set(databaseUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // let signup activity display next screen now
                                                signupScreen.showNextScreen();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                signupScreen.userRegistrationFailed("Unable to add Client's data to the Client collection: " + e.getMessage());
                                            }
                                        });
                            } else {

                                signupScreen.userRegistrationFailed("User registration returned no user info");
                            }
                        } else {
                            if (task.getException() != null) {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    signupScreen.userRegistrationFailed("An account with this email already exists!");
                                    return;
                                }

                                signupScreen.userRegistrationFailed(task.getException().toString());
                            } else {
                                signupScreen.userRegistrationFailed("createUserWithEmailAndPassword failed: for unknown reasons");
                            }

                        }
                    }
                });
    }

    public void registerLandlord(String email, String password, SignupScreen signupScreen, Landlord newUser) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                newUser.setUserId(user.getUid());
                                App.getAppInstance().setUser(newUser);

                                // therefore, add the user to database
                                database = FirebaseFirestore.getInstance();

                                Map<String, Object> databaseUser = new HashMap<>();
                                databaseUser.put("firstName", newUser.getFirstName());
                                databaseUser.put("lastName", newUser.getLastName());
                                databaseUser.put("email", newUser.getEmail());
                                databaseUser.put("isSuspended", newUser.getIsSuspended());
                                databaseUser.put("suspensionDate", newUser.getSuspensionDate());
                                databaseUser.put("addressStreet", newUser.getAddress().getStreetAddress());
                                databaseUser.put("addressCity", newUser.getAddress().getCity());
                                databaseUser.put("country", newUser.getAddress().getCountry());
                                databaseUser.put("postalCode", newUser.getAddress().getPostalCode());
                                databaseUser.put("voidCheque", newUser.getVoidCheque());
                                databaseUser.put("description", newUser.getDescription());
                                databaseUser.put("requests", null);
                                databaseUser.put("ratingSum", newUser.getLandlordRatingSum());
                                databaseUser.put("numOfRatings", newUser.getNumOfRatings());

                                database.collection("Landlords").document(newUser.getUserId())
                                        .set(databaseUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // let signup activity display next screen now
                                                signupScreen.showNextScreen();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                signupScreen.userRegistrationFailed("Unable to add Landlord's data to the Landlord collection: " + e.getMessage());
                                            }
                                        });
                            } else {
                                signupScreen.userRegistrationFailed("User registration returned no user info");
                            }
                        } else {
                            if (task.getException() != null) {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    signupScreen.userRegistrationFailed("An account with this email already exists!");
                                    return;
                                }

                                signupScreen.userRegistrationFailed(task.getException().toString());
                            } else {
                                signupScreen.userRegistrationFailed("createUserWithEmailAndPassword failed: for unknown reasons");
                            }

                        }
                    }
                });
    }

    public void logInUser(String email, String password, LoginScreen loginScreen) {
        Log.d("AuthActions", "Attempting to log in user with email: " + email);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("AuthActions", "Login successful for email: " + email);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Log.d("AuthActions", "Retrieving user data for userId: " + user.getUid());
                        firebaseRepository.USER.getUserById(user.getUid(), loginScreen);
                    } else {
                        Log.e("AuthActions", "FirebaseUser is null after successful login");
                        loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "FirebaseUser is null after successful login.");
                    }
                } else {
                    if (task.getException() != null) {
                        Log.e("AuthActions", "Login failed for email: " + email, task.getException());
                        loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "Login failed: " + task.getException().getMessage());
                    } else {
                        Log.e("AuthActions", "Login failed for email: " + email + "for unknown reasons");
                        loginScreen.dbOperationFailureHandler(UserHandler.dbOperations.USER_LOG_IN, "Login failed for unknown reasons.");
                    }
                }
            }
        });
    }

}
