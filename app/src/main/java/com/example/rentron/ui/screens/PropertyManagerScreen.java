package com.example.rentron.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.models.inbox.Ticket;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;

import java.util.ArrayList;
import java.util.List;

public class PropertyManagerScreen extends UIScreen implements StatefulView {

    int numOfButtons;
    ListView ticketListView;
    List<Ticket> ticketsData;
    public final static String TICKET_OBJ_INTENT_KEY = "ticket";
    // DB operations performed by PropertyManagerScreen
    public enum dbOperations {
        GET_TICKETS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_property_manager_screen);

        App.getInboxHandler().updatePropertyManagerInbox(this);

        ticketListView = findViewById(R.id.ticketList);
        ticketListView.setOnItemClickListener((parent, view, position, id) -> {

            // guard-clause
            if (ticketsData == null) {
                Log.e("setOnItemClickListener", "ticketsData is null");
                displayErrorToast("No tickets available");
            }

            // get the ticket
            Ticket ticket = null;

            try {
                ticket = ticketsData.get(position);

                // ticket = tickets[pos]
                Intent ticketScreenIntent = new Intent(getApplicationContext(), TicketScreen.class);
                ticketScreenIntent.putExtra(TICKET_OBJ_INTENT_KEY, ticket);
                startActivity(ticketScreenIntent);
            } catch (Exception e) {
                Log.e("setOnItemClickListener", "unable to create ticket object: " + e.getMessage());
                displayErrorToast("Unable to process request!");
            }

        });
    }

    @Override
    public void updateUI() {
        // reload tickets anytime update UI is called
        displayTickets();
    }

    @Override
    public void showNextScreen() {}

    public void clickLogout(View view) {
        // handle user logout
        App.getAppInstance().logoutUser();
        // take user back to intro screen
        Intent intent = new Intent(this, IntroScreen.class);
        startActivity(intent);
    }

    private void displayTickets() {
        try {
            ticketsData = App.getPropertyManagerInbox().getListOfTickets();

            Log.e("NUMBER TICKETS", String.valueOf(ticketsData.size()));

            List<String> ticketTitles = new ArrayList<String>();

            for (Ticket eachTicket : ticketsData) {
                ticketTitles.add(eachTicket.getTitle());
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.text_view, ticketTitles);
            ticketListView.setAdapter(arrayAdapter);
        } catch (Exception e) {
            Log.e("displayTickets", "unable to display tickets: " + e.getMessage());
        }
    }

    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == dbOperations.GET_TICKETS) {
            updateUI();
            displaySuccessToast((String) payload);
        }
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        displayErrorToast((String) payload);
    }

}
