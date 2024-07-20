package com.example.rentron.ui.screens;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.TicketEntityModel;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.Request;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.utils.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MakeTicket extends UIScreen implements StatefulView {
    Request requestData;
    Request trueRequest;

    TicketEntityModel ticket;
    EditText titleText;
    EditText descriptionText;
    TextView clientName;
    TextView landlordName;
    TextView dateOfRequest;
    Button submitTicket;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_ticket);

        //Variables for displaying request information
        clientName = findViewById(R.id.client_name);
        landlordName = findViewById(R.id.landlord_name);
        dateOfRequest = findViewById(R.id.property_date);

        //Variables for the user writing tickets
        titleText = findViewById(R.id.title_of_ticket);
        descriptionText = findViewById(R.id.ticket_writer);

        //Buttons
        submitTicket = findViewById(R.id.send_ticket_button);
        backButton = findViewById(R.id.backButtonTicket);

        try {
            requestData = (Request) getIntent().getSerializableExtra("REQUEST_DATA_ARG_KEY");
            updateUI();
        } catch (Exception e) {
            Log.e("MakeTicket", String.valueOf(e));
            Toast.makeText(getApplicationContext(), "Unable to retrieve the property info!", Toast.LENGTH_LONG).show();
        }

        submitTicket.setOnClickListener(view -> sendTicket());

        backButton.setOnClickListener(v -> finish());

    }

    //Creates a ticket
    private void sendTicket(){
        String title = titleText.getText().toString();
        String description = descriptionText.getText().toString();
        String landlordID = requestData.getLandlordInfo().getLandlordId();
        String clientID = requestData.getClientInfo().getClientId();
        Date date = Utilities.getTodaysDate();


        ticket = new TicketEntityModel(null, title, description, clientID, landlordID, date);
        App.getInboxHandler().addNewTicket(ticket, this);

        Toast.makeText(getApplicationContext(), "Ticket Sent!", Toast.LENGTH_LONG).show();
        requestData.setTicketSubmitted(true);
        List<Request> requests = ((Client) App.getUser()).REQUESTS.getCompletedRequests();
        for (int x = 0; x < requests.size(); x++){
            if (requests.get(x).getRequestID().equals(requestData.getRequestID())){
                trueRequest = requests.get(x);
                trueRequest.setTicketSubmitted(true);
            }
        }
        App.getPrimaryDatabase().REQUESTS.updateTicketStatus(trueRequest);
        this.finish();
    }

    // UI Methods-----------------------------------------------------------------------------------------------
    //sets the values of the request information
    @Override
    public void updateUI(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        clientName.setText("Client Name: " + requestData.getClientInfo().getClientName());
        landlordName.setText("Landlord Name: " + requestData.getLandlordInfo().getLandlordName());
        dateOfRequest.setText("Date: " + dateFormat.format(requestData.getRequestDate()));
    }

    @Override
    public void showNextScreen() {

    }

    // Firebase Methods-----------------------------------------------------------------------------------------------
    /**
     * Method to handle success of a DB operation
     *
     * @param dbOperation
     * @param payload
     */
    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        App.getAppInstance().getCompletedRequestsScreen().notifyDataChanged();
        displaySuccessToast((String) payload);
    }

    /**
     * Method to handle failure of a DB operation
     *
     * @param dbOperation
     * @param payload
     */
    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        displayErrorToast((String) payload);
    }
}
