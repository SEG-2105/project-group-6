package com.example.rentron.ui.screens.completed_requests;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.RequestHandler;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.Request;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;

import java.util.ArrayList;
import java.util.List;

public class CompletedRequestsScreen extends UIScreen implements StatefulView {

    // Variable Declaration
    /**
     * the map that contains the current LANDLORD's Requests
     */
    private List<Request> requestsData;

    /**
     * the list that will hold the requests
     */
    private List<Request> requests;

    /**
     * the array adapter for the list view of the Landlord completed requests
     */
    private CompletedRequestsAdapter completedRequestsAdapter;

    /**
     * the array adapter for the list view of the Client completed requests
     */
    private CompletedRequestsAdapterClient completedRequestsAdapterClient;

    /**
     * the back button icon
     */
    ImageButton backButton;

    //----------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_requests_screen);
        App.getAppInstance().setCompletedRequestsScreen(this);

        // Initialization
        requestsData = new ArrayList<Request>();
        backButton = findViewById(R.id.backButton);

        // Process: loading the Requests data
        loadCompletedRequestsData();

        // Process: populating the Requests ListView
        populateCompletedRequestsList();

        // Process: setting onClick method for back button
        backButton.setOnClickListener(v -> finish());

    }

    /**
     * this helper method retrieves the current LANDLORD's Requests
     */
    private void loadCompletedRequestsData() {

        // Process: checking if current user is a LANDLORD
        if (App.getUser() instanceof Landlord ) { //is LANDLORD
            // Initialization: setting requestsData to the list of completed requests
            this.requestsData = ((Landlord) App.getUser()).REQUESTS.getCompletedRequests();
        }
        // Process: checking if current user is a CLIENT
        else if (App.getUser() instanceof Client) { //is CLIENT
            // Initialization: setting requestsData to the list of completed requests
            this.requestsData = ((Client) App.getUser()).REQUESTS.getCompletedRequests();
        }
        else { //if not landlord//client -> error-handling
            Log.e("CompletedRequestsScreen", "Can't show completed requests; Current logged-in user is not a LANDLORD or CLIENT");

            // Output
            displayErrorToast("No completed requests available to be displayed!");
        }

    }

    /**
     * this helper method populates the Requests list
     */
    private void populateCompletedRequestsList() {

        // Variable Declaration
        this.requests = new ArrayList<Request>();
        ListView completedRequestsList = findViewById(R.id.completedListView);

        // Process: checking for landlord or client
        if (App.getUser() instanceof Client) { //client

            // Initialization: setting the adapter
            completedRequestsAdapterClient = new CompletedRequestsAdapterClient(this, R.layout.activity_completed_requests_client_list_item, this.requests);

            // Process: attaching the adapter to the ListView
            completedRequestsList.setAdapter(completedRequestsAdapterClient);

            // Process: looping through the map of data
            for (Request request: this.requestsData) {
                completedRequestsAdapterClient.add(request); //adding the requestData to the list
            }

        }
        else if (App.getUser() instanceof Landlord) { //landlord

            // Initialization: setting the adapter
            completedRequestsAdapter = new CompletedRequestsAdapter(this, R.layout.activity_completed_requests_list_item, this.requests);

            // Process: attaching the adapter to the ListView
            completedRequestsList.setAdapter(completedRequestsAdapter);

            // Process: looping through the map of data
            for (Request request: this.requestsData) {
                completedRequestsAdapter.add(request); //adding the requestData to the list
            }

        }

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void showNextScreen() {

    }

    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {

        if (dbOperation == RequestHandler.dbOperations.ADD_REQUEST) {

            // Output: successfully add new request
            displayErrorToast("Successfully added request!");

        }
        else if (dbOperation == RequestHandler.dbOperations.REMOVE_REQUEST) {

            // Output: successfully removed request
            displayErrorToast("Successfully removed request!");

        }
        else if (dbOperation == RequestHandler.dbOperations.UPDATE_REQUEST) {

            // Output: successfully updated request
            displayErrorToast("Successfully updated request!");

        }
        else if (dbOperation == RequestHandler.dbOperations.GET_REQUEST_BY_ID) {

            // Output: successfully retrieved request
            displayErrorToast("Successfully retrieved request!");

        }
        else { //other op

            // Output
            displayErrorToast((String) payload);

        }

    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        if (dbOperation == RequestHandler.dbOperations.ADD_REQUEST) {

            // Output: failed to add new request
            displayErrorToast("Failed to add request!");

        }
        else if (dbOperation == RequestHandler.dbOperations.REMOVE_REQUEST) {

            // Output: failed to remove request
            displayErrorToast("Failed to remove request!");

        }
        else if (dbOperation == RequestHandler.dbOperations.UPDATE_REQUEST) {

            // Output: failed to update request
            displayErrorToast("Failed to update request!");

        }
        else if (dbOperation == RequestHandler.dbOperations.GET_REQUEST_BY_ID) {

            // Output: failed to get request
            displayErrorToast("Failed to get request!");

        }
        else { //other error

            // Output
            displayErrorToast((String) payload);

        }
    }

    public void notifyDataChanged(){
        populateCompletedRequestsList();
    }

}
