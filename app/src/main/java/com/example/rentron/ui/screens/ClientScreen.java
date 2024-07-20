package com.example.rentron.ui.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.PropertyHandler;
import com.example.rentron.data.models.Client;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.ui.screens.completed_requests.CompletedRequestsScreen;
import com.example.rentron.ui.screens.pending_requests.PendingRequestsClientScreen;
import com.example.rentron.ui.screens.search.SearchScreen;

public class ClientScreen extends UIScreen implements StatefulView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_screen);

        // initiate loading of searchable properties (so we have them before client goes to search screen)
        App.PROPERTY_HANDLER.dispatch(PropertyHandler.dbOperations.ADD_PROPERTIES_TO_SEARCH_LIST, null, this);

        attachOnClickListeners();

        TextView welcomeMessage = (TextView) findViewById(R.id.welcome_message_client);
        if (App.getClient() != null) {
            welcomeMessage.setText("Welcome " + App.getClient().getFirstName() + ", you're logged in as a CLIENT! ");
        }
    }

    private void attachOnClickListeners() {

        // create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("NOTICE");
        // set the icon for the alert dialog
        builder.setIcon(R.drawable.rentron);

        Button searchPropertiesBtn = (Button) findViewById(R.id.searchPropertyButton);
        searchPropertiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchScreen.class));
            }
        });

        Button pendingRequestsBtn = (Button) findViewById(R.id.viewPendingRequestsButton);
        pendingRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Client) App.getUser()).REQUESTS.getClientsPendingRequests().size() != 0)
                    startActivity(new Intent(getApplicationContext(), PendingRequestsClientScreen.class));
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

        Button completedRequestsBtn = (Button) findViewById(R.id.viewCompletedRequestsButton);
        completedRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Client) App.getUser()).REQUESTS.getCompletedRequests().size() != 0)
                    startActivity(new Intent(getApplicationContext(), CompletedRequestsScreen.class));
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
    }

    public void clickLogout(View view) {
        // handle user logout
        App.getAppInstance().logoutUser();
        // take user back to intro screen
        Intent intent = new Intent(this, IntroScreen.class);
        startActivity(intent);
    }

    @Override
    public void updateUI() {}

    @Override
    public void showNextScreen() {}

    /**
     * Method to handle success of a DB operation
     */
    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        displaySuccessToast((String) payload);
        Log.e("clientScreen", "DB op: " + dbOperation + " success");
    }

    /**
     * Method to handle failure of a DB operation
     */
    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        displayErrorToast((String) payload);
        Log.e("clientScreen", "DB op: " + dbOperation + " failed: " + payload);
    }
}
