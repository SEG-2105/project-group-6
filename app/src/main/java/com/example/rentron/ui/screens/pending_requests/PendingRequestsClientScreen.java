package com.example.rentron.ui.screens.pending_requests;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.Request;
import com.example.rentron.ui.core.UIScreen;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestsClientScreen extends UIScreen {

    // Variable Declaration
    /**
     * the map that contains the current CLIENT's Requests
     */
    private List<Request> requestsData;

    /**
     * the list that will hold the requests
     */
    private List<Request> requests;

    /**
     * the array adapter for the list view of the pending requests
     */
    private PendingRequestsAdapterClient pendingRequestsAdapterClient;

    /**
     * the back button icon
     */
    ImageButton backButton;

    //----------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests_client_screen);

        // Initialization
        requestsData = new ArrayList<Request>();
        backButton = findViewById(R.id.backButton7);

        // Process: loading the Requests data
        loadPendingRequestsData();

        // Process: populating the Requests ListView
        populatePendingRequestsList();

        // Process: setting onClick method for back button
        backButton.setOnClickListener(v -> finish());

    }

    /**
     * this helper method retrieves the current CLIENT's Requests
     */
    private void loadPendingRequestsData() {

        // Process: checking if current user is a CLIENT
        if (App.getUser() instanceof Client) { //is CLIENT
            // Initialization: setting requestsData to the list
            this.requestsData = ((Client) App.getUser()).REQUESTS.getClientsPendingRequests();
        }
        else { //not a client -> error-handling
            Log.e("PendingRequests Client", "Can't show pending requests; Current logged-in user is not a CLIENT");

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
        ListView pendingRequestsList = findViewById(R.id.pendingClientsListView);

        // Initialization: setting the adapter
        pendingRequestsAdapterClient = new PendingRequestsAdapterClient(this, R.layout.activity_completed_requests_list_item, this.requests);

        // Process: attaching the adapter to the ListView
        pendingRequestsList.setAdapter(pendingRequestsAdapterClient);

        // Process: looping through the map of data
        for (Request request: this.requestsData) {
            pendingRequestsAdapterClient.add(request); //adding the requestData to the list
        }

    }
}
