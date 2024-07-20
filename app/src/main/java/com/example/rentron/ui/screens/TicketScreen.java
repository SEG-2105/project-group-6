package com.example.rentron.ui.screens;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.UserHandler;
import com.example.rentron.data.models.inbox.Ticket;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.utils.Preconditions;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TicketScreen extends UIScreen implements StatefulView{
    private Button dismissButton;
    private Button banTempButton;
    private Button banPermButton;
    private ImageButton backButton;
    private DatePickerDialog datePickerDialog;
    DatePickerDialog.OnDateSetListener dateSetListener;
    Ticket ticketData;
    String[] clientAndLandlordNames;
    public final static String INFINITE_SUSPENSION_DATE = "01/01/9999";
    // store landlord's suspension date if landlord already suspended
    private String landlordSuspensionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_screen);
        dismissButton = (Button) findViewById(R.id.dismiss);
        banTempButton = (Button) findViewById(R.id.ban_landlord);
        banPermButton = (Button) findViewById(R.id.ban_permament);
        backButton = (ImageButton) findViewById(R.id.cs_button_back);

        // get the ticket data
        try {
            ticketData = (Ticket) getIntent().getSerializableExtra(PropertyManagerScreen.TICKET_OBJ_INTENT_KEY);
        } catch (Exception e) {
            Log.e("TicketScreen", "unable to create ticket object");
            displayErrorToast("Unable to display ticket!");
        }

        // initialize array to hold client and landlord names
        clientAndLandlordNames = new String[2];

        // get client and landlord user names
        getClientAndLandlordNames();

        banTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(TicketScreen.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        banPermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suspendLandlord(INFINITE_SUSPENSION_DATE);
            }});

        dateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                String suspendString = Integer.toString(month + 1) + "/" + Integer.toString(dayOfMonth) + "/" + Integer.toString(year);
                Date suspendDate = null;
                try {
                    suspendDate = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US).parse(suspendString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // suspend the landlord
                suspendLandlord(suspendString);
            }
        };

        /**
         * On-Click Listener for Dismiss Button in Ticket Screen
         * When clicked, the ticket will be dismissed, and user will be redirected back to property manager screen
         */
        dismissButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getInboxHandler().removeTicket(ticketData.getId()); //Remove ticket from primary database of Property Manager Inbox
                showNextScreen(); //Redirect to Property Manager Screen
            }
        });

        /**
         * Back button
         */
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // go to previous screen
                finish();
            }
        });
    }

    @Override
    public void updateUI() {
        // if we have now received both client and landlord names
        if (
                Preconditions.isNotEmptyString(clientAndLandlordNames[0]) &&
                        Preconditions.isNotEmptyString(clientAndLandlordNames[1])
        ) {
            try {
                updateTicketScreen(ticketData.getTitle(), clientAndLandlordNames[0], clientAndLandlordNames[1], ticketData.getRequestId(), ticketData.getDescription());
            }catch (Exception e) {
                Log.e("TicketScreen", "unable to create ticket object");
                displayErrorToast("Unable to display ticket!");
            }
        }
    }

    /**
     * Updates the text on the ticket screen
     * @param title
     * @param client
     * @param landlord
     * @param property
     * @param description
     */
    @SuppressLint("ResourceAsColor")
    public void updateTicketScreen(String title, String client, String landlord, String property, String description) {

        // sets the ticket header title
        TextView textTitle = (TextView)findViewById(R.id.ticketHeader);
        textTitle.setText(title);

        // sets the text for client name
        TextView textClient = (TextView)findViewById(R.id.clientTicketName);
        textClient.setText(client);

        // sets the text for landlord name
        TextView textLandlord = (TextView)findViewById(R.id.landlordTicketName);
        textLandlord.setText(landlord);

        // sets the text for property
        // TextView textProperty = (TextView)findViewById(R.id.propertyTicketName);
        // textProperty.setText(property);

        // sets the text for description
        TextView textDescription = (TextView)findViewById(R.id.ticketDescription);
        textDescription.setText(description);

        // if landlord is suspended display suspension date
        displayLandlordSuspensionDate();
    }

    // if landlord is suspended, then show Property Manager the landlord's suspension date
    private void displayLandlordSuspensionDate() {
        // if there is landlord suspension date
        TextView csLandlordSuspension = (TextView) findViewById(R.id.csLandlordSuspensionInfo);
        if (this.landlordSuspensionDate != null && !this.landlordSuspensionDate.isEmpty() && !this.landlordSuspensionDate.equalsIgnoreCase("null")) {
            String landlordSuspended = "Landlord is already suspended until: " + this.landlordSuspensionDate;
            csLandlordSuspension.setText(landlordSuspended);
            csLandlordSuspension.setVisibility(View.VISIBLE);
        } else {
            csLandlordSuspension.setVisibility(View.GONE);
        }
    }

    /**
     * This method creates a new activity with the updated property manager screen & inbox
     */
    @Override
    public void showNextScreen() {
        startActivity(new Intent(getApplicationContext(), PropertyManagerScreen.class));
    }

    private void getClientAndLandlordNames() {

        // we need ticket data to retrieve landlord and client names by ids
        if (ticketData == null) {
            Log.e("getClientAndLandlordNames", "ticketData is null");
            displayErrorToast("not a valid ticket data");
        }

        // send the request to get id
        App.getUserHandler().getClientAndLandlordNamesByIds(ticketData.getClientId(), ticketData.getLandlordId(), this);
    }

    /**
     * Method to handle success of a DB operation
     */
    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES) {
            handleNamesRetrievalSuccess((String) payload);
        }

        if (dbOperation == UserHandler.dbOperations.GET_LANDLORD_SUSPENSION_DATE) {
            this.landlordSuspensionDate = (String) payload;
        }
    };

    /**
     * Method to handle failure of a DB operation
     */
    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        if (dbOperation == UserHandler.dbOperations.GET_CLIENT_AND_LANDLORD_NAMES) {
            displayErrorToast((String) payload);
        }
    };

    public void handleNamesRetrievalSuccess(String name) {
        if (clientAndLandlordNames == null) {
            Log.e("handleNames", "clientAndLandlordNames is null");
            displayErrorToast("Unable to process request");
        }

        // if names array is empty, set client name
        if (!Preconditions.isNotEmptyString(clientAndLandlordNames[0])) {
            clientAndLandlordNames[0] = name;
        } else {
            // set landlord's name
            clientAndLandlordNames[1] = name;
        }
    }

    private void suspendLandlord(String suspensionDate) {
        // suspend the landlord
        App.getUserHandler().suspendLandlord(ticketData.getLandlordId(), suspensionDate);
        // remove the ticket
        App.getInboxHandler().removeTicket(ticketData.getId());
        // take back to property manager screen
        showNextScreen();
    }
}
