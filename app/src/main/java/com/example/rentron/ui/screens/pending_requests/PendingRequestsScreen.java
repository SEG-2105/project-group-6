package com.example.rentron.ui.screens.pending_requests;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.RequestHandler;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Request;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestsScreen extends UIScreen implements StatefulView {

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
     * the array adapter for the list view of the pending requests
     */
    private PendingRequestsAdapter pendingRequestsAdapter;

    /**
     * the back button icon
     */
    ImageButton backButton;

    //----------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests_screen);
        App.getAppInstance().setPendingRequestsScreen(this);

        // Initialization
        requestsData = new ArrayList<Request>();
        backButton = findViewById(R.id.backButton);

        // Process: loading the Requests data
        loadPendingRequestsData();

        // Process: populating the Requests ListView
        populatePendingRequestsList();

        // Process: setting onClick method for back button
        backButton.setOnClickListener(v -> finish());

    }

    /**
     * this helper method retrieves the current LANDLORD's Requests
     */
    private void loadPendingRequestsData() {

        // Process: checking if current user is a LANDLORD
        if (App.getUser() instanceof Landlord) { //is LANDLORD
            // Initialization: setting requestsData to the list
            this.requestsData = ((Landlord) App.getUser()).REQUESTS.getPendingRequests();
        }
        else { //not a landlord -> error-handling
            Log.e("PendingRequestsScreen", "Can't show pending requests; Current logged-in user is not a LANDLORD");

            // Output
            displayErrorToast("No pending requests available to be displayed!");
        }

    }

    /**
     * this helper method populates the Requests list
     */
    private void populatePendingRequestsList() {

        // Variable Declaration
        this.requests = new ArrayList<Request>();
        ListView pendingRequestsList = findViewById(R.id.pendingListView);

        // Initialization: setting the adapter
        pendingRequestsAdapter = new PendingRequestsAdapter(this, R.layout.activity_pending_requests_list_item, this.requests);

        // Process: attaching the adapter to the ListView
        pendingRequestsList.setAdapter(pendingRequestsAdapter);

        // Process: looping through the map of data
        for (Request request: this.requestsData) {
            pendingRequestsAdapter.add(request); //adding the requestData to the list
        }

    }

    /**
     * this helper method populates the Requests list after a change has been made
     */
    private void repopulatePendingRequestsList() {

        pendingRequestsAdapter.clear(); //removing all previous data

        // Process: looping through the map of data
        for (Request request: this.requestsData) {
            pendingRequestsAdapter.add(request); //adding the requestData to the list
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

        loadPendingRequestsData();
        repopulatePendingRequestsList();

        // Process: telling adapter that requests have been updated
        pendingRequestsAdapter.notifyDataSetChanged();

        App.getAppInstance().getRequestsInProgressScreen().updateAdapter();

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
}
