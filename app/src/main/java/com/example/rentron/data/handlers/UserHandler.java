package com.example.rentron.data.handlers;

import android.util.Log;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.CreditCardEntityModel;
import com.example.rentron.data.entity_models.UserEntityModel;
import com.example.rentron.data.models.Address;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.CreditCard;
import com.example.rentron.data.models.UserRoles;
import com.example.rentron.ui.screens.TicketScreen;
import com.example.rentron.ui.screens.LoginScreen;
import com.example.rentron.ui.screens.SignupScreen;
import com.example.rentron.utils.Response;

import java.util.Calendar;

public class UserHandler {

    /**
     * DB Operations handled by this handler
     */
    public enum dbOperations {
        USER_LOG_IN,
        GET_CLIENT_AND_LANDLORD_NAMES,
        GET_LANDLORD_SUSPENSION_DATE
    }

    /**
     * Register a new Client account
     * @param signupScreen instance of login screen (required in asynchronous callback function to indicate success or failure)
     * @param userData user entity model containing all unvalidated user data
     * @param creditCardData credit card entity model containing all unvalidated credit card data
     * @return a Response object containing error or success message
     */
    public Response registerClient(SignupScreen signupScreen, UserEntityModel userData, CreditCardEntityModel creditCardData) {
        // guard clause
        if (userData == null) {
            return new Response(false, "Please complete all fields.");
        } else if (creditCardData == null) {
            return new Response(false, "Please complete all credit card fields.");
        }

        // set the appropriate role for the user
        userData.setRole(UserRoles.CLIENT);

        try {
            // Try to instantiate three objects: Address object, CreditCard object, and finally, Client object
            Client newClient = new Client(userData, new Address(userData.getAddress()), new CreditCard(creditCardData));

            // if all user data was valid, add the user to the database
            App.getPrimaryDatabase().AUTH.registerClient(userData.getEmail(), userData.getPassword(), signupScreen, newClient);

            return new Response(true, "User signup submitted!");
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
    }

    /**
     * Register a new Landlord account
     * @param signupScreen instance of login screen (required in asynchronous callback function to indicate success or failure)
     * @param userData user entity model containing all unvalidated user data
     * @param landlordShortDescription a short description provided by landlord
     * @param voidCheque void cheque image provided by landlord
     * @return a Response object containing error or success message
     */
    public Response registerLandlord(SignupScreen signupScreen, UserEntityModel userData, String landlordShortDescription, String voidCheque) {
        // guard clause
        if (userData == null) {
            return new Response(false, "Please complete all fields.");
        } else if (landlordShortDescription != null && landlordShortDescription.equals("")) {
            return new Response(false, "Please provide a short description of yourself.");
        }

        // set the appropriate role for the user
        userData.setRole(UserRoles.LANDLORD);

        try {
            // Try to instantiate two objects: Address object & the Landlord object itself
            Landlord newLandlord = new Landlord(userData, new Address(userData.getAddress()), landlordShortDescription, voidCheque);

            // if all user data was valid, add the user to the database
            App.getPrimaryDatabase().AUTH.registerLandlord(userData.getEmail(), userData.getPassword(), signupScreen, newLandlord);

            return new Response(true, "User signup submitted");
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
    }

    /**
     * Method to login user
     * @param loginScreen instance of login screen (required in asynchronous callback function to indicate success or failure)
     * @param email email address of the user
     * @param password password provided by the user
     */
    public void logInUser(LoginScreen loginScreen, String email, String password) {
        Log.d("UserHandler", "Logging in user with email: " + email);
        App.getPrimaryDatabase().AUTH.logInUser(email, password, loginScreen);
    }


    /**
     * Method to suspend landlord
     * @param landlordID Landlord involved with ticket
     * @param suspensionDate end date of suspension
     */
    public void suspendLandlord(String landlordID, String suspensionDate) {
        App.getPrimaryDatabase().USER.updateLandlordSuspension(landlordID, true, suspensionDate);
    }

    /**
     * Method to update landlord (check if date has passed)
     * @param landlord Landlord involved with ticket
     */
    public void updateLandlordSuspension(Landlord landlord) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(landlord.getSuspensionDate());

        if (landlord.getSuspensionDate() != null) { // not suspended
            if (calendar.before(Calendar.getInstance())) { // if the date has passed, change info in Firebase
                landlord.setIsSuspended(false);
                landlord.setSuspensionDate(null);
                App.getPrimaryDatabase().USER.updateLandlordSuspension(landlord.getUserId(), false, null);
            }
        }
    }

    public void getClientAndLandlordNamesByIds(String clientId, String landlordId, TicketScreen ticketScreen) {
        App.getPrimaryDatabase().USER.getClientAndLandlordNamesByIds(clientId, landlordId, ticketScreen);
    }
}
