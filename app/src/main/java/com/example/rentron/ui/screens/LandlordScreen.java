package com.example.rentron.ui.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.RequestHandler;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Request;
import com.example.rentron.data.models.User;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.ui.screens.completed_requests.CompletedRequestsScreen;
import com.example.rentron.ui.screens.properties.PropertiesListScreen;
import com.example.rentron.ui.screens.pending_requests.PendingRequestsScreen;

import java.util.ArrayList;
import java.util.List;

public class LandlordScreen extends UIScreen implements StatefulView {
    // Variable Declaration
    TextView editText;

    /**
     * the map that contains the current LANDLORD's Requests
     */
    private List<Request> requestData;

    /**
     * the list that will hold the requests
     */
    private List<Request> requests;

    /**
     * the array adapter for the list view of the requests in progress
     */
    private RequestsInProgressAdapter requestsInProgressAdapter;

    //----------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_screen);
        App.getAppInstance().setRequestsInProgressScreen(this);

        // HEADER
        editText = (TextView) findViewById(R.id.welcome_message_landlord);
        User currentUser = App.getAppInstance().getUser();
        if (App.getAppInstance().isUserAuthenticated()) { // Change text to proper welcome message when opened
            setWelcomeMessage("Welcome " + currentUser.getFirstName() + " " + currentUser.getLastName() + ", you're logged in as a LANDLORD!");
        }

        // Initialization
        attachOnClickListeners();

        // Process: loading the in progress requests
        loadRequestsInProgress();

        // Process: populate the Requests ListView
        populateRequestsInProgress();

    }

    /**
     * retrieves the current landlord's requests in progress
     */
    private void loadRequestsInProgress() {

        // Process: Check if current user is a landlord
        if (App.getUser() instanceof Landlord) { // is a LANDLORD
            // Initialization: setting requestsData to the list
            this.requestData = ((Landlord) App.getUser()).REQUESTS.getRequestsInProgress();
        } else { // not a landlord -> error-handling
            Log.e("LandlordScreen", "Current logged-in user is not a LANDLORD");
        }
    }

    /**
     * populates the list with all in progress properties that will be displayed
     */
    private void populateRequestsInProgress() {

        // variable declaration
        this.requests = new ArrayList<Request>();
        ListView requestsInProgressList = findViewById(R.id.requestsInProgress);

        // Initialization: setting adapter
        requestsInProgressAdapter = new RequestsInProgressAdapter(this, R.layout.requests_in_progress, this.requests);

        // Process: attach adapter to ListView
        requestsInProgressList.setAdapter(requestsInProgressAdapter);

        // Process: if theres no properties, display no properties in progress text
        if (requestData.size() == 0) {
            TextView noPropertiesInProgress = (TextView) findViewById(R.id.noRequestsInProgress);
            noPropertiesInProgress.setVisibility(View.VISIBLE);
        }

        // Process: loop through the map of data for the requests in progress
        for (Request request : this.requestData) {
            requestsInProgressAdapter.add(request); // adds requestData to the list
        }
    }

    /**
     * this helper method populates the Requests list after a change has been made
     */
    private void repopulatePendingRequestsList() {

        requestsInProgressAdapter.clear(); //removing all previous data

        // Process: if theres no properties, display no properties in progress text
        if (requestData.size() == 0) {
            TextView noPropertiesInProgress = (TextView) findViewById(R.id.noRequestsInProgress);
            noPropertiesInProgress.setVisibility(View.VISIBLE);
        } else {
            TextView noPropertiesInProgress = (TextView) findViewById(R.id.noRequestsInProgress);
            noPropertiesInProgress.setVisibility(View.INVISIBLE);
        }

        // Process: looping through the map of data
        for (Request request : this.requestData) {
            requestsInProgressAdapter.add(request); //adding the requestData to the list
        }

    }

    public RequestsInProgressAdapter getRequestsInProgressAdapter() {
        return requestsInProgressAdapter;
    }

    public void updateAdapter() {
        loadRequestsInProgress();
        repopulatePendingRequestsList();

        requestsInProgressAdapter.notifyDataSetChanged();
    }

    /**
     * Sets the appropriate welcome message to the user
     *
     * @param message
     */
    private void setWelcomeMessage(String message) {
        editText.setText(message, TextView.BufferType.EDITABLE);
    }

    /**
     * OnClick method for buttons
     */
    private void attachOnClickListeners() {

        // Property buttons
        Button menuButton = (Button) findViewById(R.id.viewMenuButton);
        Button offeredPropertiesButton = (Button) findViewById(R.id.viewOfferedButton);
        Button addButton = (Button) findViewById(R.id.addPropertyButton);

        //Profile Button
        Button profileButton = (Button) findViewById(R.id.landlordProfile);

        // Request buttons
        Button viewRequest = (Button) findViewById(R.id.viewCompletedRequestsButton);
        Button viewPendingRequest = (Button) findViewById(R.id.viewPendingRequestsButton);
        // View profile button
        ImageView viewLandlordProfile = (ImageView) findViewById(R.id.rentron_logo);

        // create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NOTICE");
        // set the icon for the alert dialog
        builder.setIcon(R.drawable.rentron);

        menuButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if no valid landlord logged in
                if (App.getLandlord() == null) return;

                // if landlord has no properties
                if (App.getLandlord().PROPERTIES.getMenu().size() == 0 || App.getLandlord().PROPERTIES.getMenu() == null) {

                    builder.setMessage("You have no properties!");
                    builder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else
                    // landlord has properties
                    startActivity(new Intent(getApplicationContext(), PropertiesListScreen.class));
            }
        });

        offeredPropertiesButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if no valid landlord logged in
                if (App.getLandlord() == null) return;

                // if landlord has no offered properties
                if (App.getLandlord().PROPERTIES.getOfferedProperties().size() == 0 || App.getLandlord().PROPERTIES.getOfferedProperties() == null) {
                    builder.setMessage("You have no offered properties!");
                    builder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // initialize a new intent
                    Intent intent = new Intent(getApplicationContext(), PropertiesListScreen.class);
                    // specify that we want to display offered properties
                    intent.putExtra(PropertiesListScreen.PROPERTIES_TYPE_ARG_KEY, PropertiesListScreen.PROPERTIES_TYPE.OFFERED_PROPERTIES.toString());
                    // display the offered properties list
                    startActivity(intent);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewPropertyScreen.class)); //show new property screen
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandlordInfoScreen.class)); //Show Landlord's Profile
            }
        });

        viewRequest.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Landlord) App.getUser()).REQUESTS.getCompletedRequests().size() != 0)
                    startActivity(new Intent(getApplicationContext(), CompletedRequestsScreen.class)); //show completed requests
                else {
                    builder.setMessage("You have no completed requests!");
                    builder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        viewPendingRequest.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Landlord) App.getUser()).REQUESTS.getPendingRequests().size() != 0)
                    startActivity(new Intent(getApplicationContext(), PendingRequestsScreen.class)); //show pending requests
                else {
                    builder.setMessage("You have no pending requests!");
                    builder.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        viewLandlordProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandlordInfoScreen.class));
            }
        });
    }

    /**
     * When the user clicks the logout button, it will take back to the intro screen
     *
     * @param view
     */
    public void clickLogout(View view) {
        // handle user logout
        App.getAppInstance().logoutUser();
        // take user back to intro screen
        Intent intent = new Intent(this, IntroScreen.class);
        startActivity(intent);
    }

    @Override
    public void updateUI() {
    }

    @Override
    public void showNextScreen() {

    }

    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == RequestHandler.dbOperations.UPDATE_REQUEST) {

            // Output: successfully completed a request
            displayErrorToast("Successfully completed request!");

        } else { // other op
            displayErrorToast((String) payload);
        }
        updateAdapter();
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        if (dbOperation == RequestHandler.dbOperations.UPDATE_REQUEST) {

            // Output: failed to update request
            displayErrorToast("Failed to update request!");

        }
    }
}
