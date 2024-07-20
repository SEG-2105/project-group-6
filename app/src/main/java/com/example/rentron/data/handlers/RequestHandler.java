package com.example.rentron.data.handlers;

import android.util.Log;

import com.example.rentron.app.App;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.Request;
import com.example.rentron.data.models.UserRoles;
import com.example.rentron.data.models.requests.RequestItem;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Utilities;
import java.util.Map;

public class RequestHandler {

    public enum dbOperations {
        ADD_REQUEST,
        REMOVE_REQUEST,
        GET_REQUEST_BY_ID,
        UPDATE_REQUEST,
        LOAD_LANDLORD_REQUESTS,
        LOAD_CLIENT_REQUESTS,
        RATE_LANDLORD,
        ERROR
    }

    private StatefulView uiScreen;

    /**
     * Using the Dispatch-Action Pattern to handle actions dispatched to Property Handler
     * @param operationType one of the specified DB operations handled by RequestHandler
     * @param payload an input data for the handler's operation
     * @param uiScreen instance of the view which needs to know of the operation's success or failure
     */
    public void dispatch(RequestHandler.dbOperations operationType, Object payload, StatefulView uiScreen) {

        // guard-clause
        if (Preconditions.isNotNull(uiScreen)) {

            // set the ui screen, so it can be interacted with later on
            this.uiScreen = uiScreen;

            try {
                switch (operationType) {

                    case ADD_REQUEST:
                        addNewRequest();
                        break;

                    case REMOVE_REQUEST:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            // Process: removing request from Firebase
                            App.getPrimaryDatabase().REQUESTS.removeRequest((String) payload);
                        }
                        else {
                            handleActionFailure( operationType, "Invalid Request ID provided");
                        }
                        break;

                    case GET_REQUEST_BY_ID:
                        if (Preconditions.isNotNull(payload) && payload instanceof String)
                            // Process: getting Request from Firebase, given the requestID
                            App.getPrimaryDatabase().REQUESTS.getRequestById((String) payload);
                        break;

                    case UPDATE_REQUEST:
                        if (Preconditions.isNotNull(payload) && payload instanceof Request) {
                            // Process: updating request in Firebase
                            App.getPrimaryDatabase().REQUESTS.updateRequest((Request) payload);
                        }
                        else {
                            handleActionFailure( operationType, "Invalid Request object provided");
                        }
                        break;

                    case LOAD_LANDLORD_REQUESTS:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            // Process: loading landlord requests in Firebase
                            App.getPrimaryDatabase().REQUESTS.loadLandlordRequests((String) payload);
                        }
                        else {
                            handleActionFailure( operationType, "Invalid String object provided");
                        }
                        break;
                    case LOAD_CLIENT_REQUESTS:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            // Process: loading client requests in Firebase
                            App.getPrimaryDatabase().REQUESTS.loadClientRequests((String) payload);
                        }
                        else {
                            handleActionFailure( operationType, "Invalid String object provided");
                        }
                        break;

                    default:
                        Log.e("RequestHandler dispatch", "Action not implemented yet");
                }
            }
            catch (Exception e) {
                Log.e("RequestHandler Dispatch", "Exception: " + e.getMessage());
                uiScreen.dbOperationFailureHandler(RequestHandler.dbOperations.ERROR, "Request failed: " + e.getMessage());
            }

        }
        else { //UI Screen doesn't exist
            Log.e("RequestHandler Dispatch", "Invalid instance provided for uiScreen");
        }

    }

    /**
     * Method which is called AFTER a successful database operation to make updates locally
     * @param operationType type of database operation which was successful
     * @param payload data for making changes locally
     */
    public void handleActionSuccess(RequestHandler.dbOperations operationType, Object payload) {
        // ensure we have a valid uiScreen to inform of success
        if(Preconditions.isNotNull(uiScreen)) {

            try {
                switch (operationType) {

                    case ADD_REQUEST:
                        if (Preconditions.isNotNull(payload) && payload instanceof Request && App.getUser().getRole() != UserRoles.PROPERTY_MANAGER) {

                            // Process: checking for landlord or client
                            if (App.getUser().getRole() == UserRoles.CLIENT) { //client
                                // add request to client's list of requests
                                if (App.getClient() != null) {
                                    App.getClient().REQUESTS.addRequest((Request) payload);
                                }
                            }
                            else { //landlord
                                // add request to landlord's list of requests
                                if (App.getLandlord() != null) {
                                    App.getLandlord().REQUESTS.addRequest((Request) payload);
                                }

                                // LOCALLY: adding request id to array in landlord
                                ((Landlord) App.getUser()).REQUESTS.addRequest((Request) payload);
                            }

                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Request added successfully!");

                        }
                        else { //the PROPERTY_MANAGER is trying to add a request, or the Request is invalid
                            handleActionFailure(operationType, "Invalid request object provided, or PROPERTY_MANAGER attempting to add request");
                        }
                        break;

                    case REMOVE_REQUEST:
                        if (Preconditions.isNotNull(payload) && payload instanceof String && App.getUser().getRole() == UserRoles.LANDLORD) {
                            // LOCALLY: removing request id from array in landlord
                            ((Landlord) App.getUser()).REQUESTS.removeRequest((String) payload);

                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Request removed successfully!");
                        }
                        else { //someone other than a LANDLORD is removing the request, OR the requestID is invalid
                            handleActionFailure(operationType, "Invalid request ID, or CLIENT/PROPERTY_MANAGER attempting to remove request");
                        }
                        break;

                    case UPDATE_REQUEST:
                        if (Preconditions.isNotNull(payload) && payload instanceof Request  && App.getUser().getRole() == UserRoles.LANDLORD) {
                            //Locally updating request
                            ((Landlord) App.getUser()).REQUESTS.updateRequest((Request) payload);

                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Updated request info successfully!");
                        } else {
                            handleActionFailure( operationType, "Invalid request instance provided");
                        }
                        break;

                    case GET_REQUEST_BY_ID:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            //do something here

                            uiScreen.dbOperationSuccessHandler(operationType, "Getting request by ID was a success!");
                        } else {
                            handleActionFailure(operationType, "Invalid payload for getRequest");
                        }
                        break;

                    case LOAD_LANDLORD_REQUESTS:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {

                            uiScreen.dbOperationSuccessHandler(operationType, "Loading landlord properties was a success!");
                        }
                        else {
                            handleActionFailure( operationType, "Invalid String object provided");
                        }
                        break;

                    case LOAD_CLIENT_REQUESTS:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            uiScreen.dbOperationSuccessHandler(operationType, "Loading client properties was a success!");
                        }
                        else {
                            handleActionFailure( operationType, "Invalid String object provided");
                        }
                        break;

                }
            }
            catch (Exception e) { //error-handling
                Log.e("handleActionSuccess", "Success handler exception: " + e.getMessage());
                uiScreen.dbOperationFailureHandler(RequestHandler.dbOperations.ERROR, "Failed to process request");
            }

        }
        else { //UI Screen doesn't exist
            Log.e("handleActionSuccess", "No UI Screen initialized");
        }
    }

    /**
     * Method which is called AFTER a failure in a database operation to inform UI
     * @param operationType type of database operation which failed
     * @param message a descriptive error message for the developers and analyst (not for client or landlord)
     */
    public void handleActionFailure(RequestHandler.dbOperations operationType, String message) {
        // ensure we have a valid uiScreen to inform of failure
        if(Preconditions.isNotNull(uiScreen)) {

            String tag = "handleActionFailure";
            String userMessage = "Failed to process request";

            switch (operationType) {

                case ADD_REQUEST:
                    tag = "addRequest";
                    userMessage= "Failed to add request!";
                    break;

                case REMOVE_REQUEST:
                    tag = "errorRemovingRequest";
                    userMessage = "Failed to remove request!";
                    break;

                case UPDATE_REQUEST:
                    tag = "updateRequest";
                    userMessage = "Failed to update request info!";
                    break;

                case GET_REQUEST_BY_ID:
                    tag = "errorGetRequest";
                    userMessage = "Failed to get request!";
                    break;

                case LOAD_LANDLORD_REQUESTS:
                    tag = "errorLoadingLandlordRequest";
                    userMessage = "Failed to load landlord's requests!";
                    break;

                case LOAD_CLIENT_REQUESTS:
                    tag = "errorLoadingClientRequest";
                    userMessage = "Failed to load client's requests!";
                    break;

                default:
                    Log.e("handleActionSuccess", "Action not implemented yet");

            }

            // send error
            Log.e(tag, message);
            uiScreen.dbOperationFailureHandler(operationType, userMessage);

        }
        else { //UI Screen doesn't exist
            Log.e("handleActionFailure", "No UI Screen initialized");
        }
    }

    public void updateLandlordRating(String requestId, String landlordId, Double newRating, StatefulView uiScreen){
        this.uiScreen = uiScreen;

        if (App.getClient() != null){
            App.getPrimaryDatabase().REQUESTS.updateLandlordRating(requestId, landlordId, newRating); //firebase
            App.getClient().REQUESTS.getRequest(requestId).getSuccessObject().setRating(newRating);
            App.getClient().REQUESTS.getRequest(requestId).getSuccessObject().setIsRated(true);
        }
    }

    public void handleUpdateLandlordRatingSuccess(){
        Log.e("RequestHandler", uiScreen.toString());
        uiScreen.dbOperationSuccessHandler(dbOperations.RATE_LANDLORD, "Updating landlord rating was a success!");
    }

    public void handleUpdateLandlordRatingFailure(String errorMessage){
        Log.e("RequestHandler", uiScreen.toString());
        uiScreen.dbOperationFailureHandler(dbOperations.RATE_LANDLORD, errorMessage);
    }

    private void addNewRequest() {
        // ensure valid client
        if (App.getClient() != null) {
            // get the clients cart
            Map<RequestItem, Boolean> cart = App.getClient().getCart();
            // ensure cart isn't empty or uninitialized
            if (Preconditions.isNotNull(cart) && !cart.isEmpty()) {
                // create a new Request
                Request request = new Request();
                // set info of client making request
                request.setClient(App.getClient());
                // set today's date
                request.setDate(Utilities.getTodaysDate());
                // flag to set landlord info
                boolean isLandlordInfoAdded = false;
                // iterate through cart and add all properties
                for (RequestItem requestItem : cart.keySet()) {
                    // if landlord info not added, add it
                    if (!isLandlordInfoAdded) {
                        // set landlord info
                        request.setLandlordInfo(requestItem.getSearchPropertyItem().getLandlord());
                        isLandlordInfoAdded = true;
                    }

                    // Add propertyInfo and quantity to hashmap in request
                    request.addPropertyQuantity(requestItem.getSearchPropertyItem().getProperty(),requestItem.getQuantity());
                }
                // once request has all propertyIds in it, we add the request remotely
                App.getPrimaryDatabase().REQUESTS.addRequest(request);
            } else {
                handleActionFailure(dbOperations.ADD_REQUEST, "Cart is empty or uninitialized!");
            }
        }
    }

}
