package com.example.rentron.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.User;
import com.example.rentron.ui.core.UIScreen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WelcomeScreen extends UIScreen {

    TextView editText;
    public final static int INFINITE_YEAR = 9999;
    public final static String LANDLORD_SUSPENSION_DATE_KEY = "LANDLORD_SUSPENSION_DATE_KEY";
    public final static String LANDLORD_NAME_KEY = "LANDLORD_NAME_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome_screen);
        editText = (TextView) findViewById(R.id.welcome_message_landlord);

        // First handle a suspended landlord logging in, since in that case no retrieval of additional info is required
        // if we have a value for landlord suspension date to be displayed - means Landlord is suspended
        if (getIntent() != null & getIntent().getExtras() != null && getIntent().getExtras().get(LANDLORD_SUSPENSION_DATE_KEY) != null) {
            setWelcomeMessage("Welcome Landlord " + getIntent().getExtras().get(LANDLORD_NAME_KEY)  + "!");
            showSuspensionMessage(String.valueOf(getIntent().getExtras().get(LANDLORD_SUSPENSION_DATE_KEY)));

            // no need to do anything else
            return;
        }

        User currentUser = App.getAppInstance().getUser();

        // Change text to proper welcome message when opened
        if (App.getAppInstance().isUserAuthenticated()) {
            setWelcomeMessage("Welcome " + currentUser.getRole() + ", " + currentUser.getFirstName() + " " + currentUser.getLastName() + "!");
        }

    }

    /**
     * Sets the welcome message in the header
     * @param message
     */
    private void setWelcomeMessage(String message) {
        editText.setText(message, TextView.BufferType.EDITABLE);
    }

    /**
     * Handles the logout action
     * @param view
     */
    public void clickLogout(View view) {
        // handle user logout
        App.getAppInstance().logoutUser();
        // take user back to intro screen
        Intent intent = new Intent(this, IntroScreen.class);
        startActivity(intent);
    }

    /**
     * this method shows the suspension message to the user
     * @param suspensionDateValue String value representing Landlord's suspension date
     *  the end date of the suspension
     */
    private void showSuspensionMessage(String suspensionDateValue) {

        // try to parse suspension date
        try {
            Date suspensionDate = new SimpleDateFormat("MM/dd/yyyy").parse(suspensionDateValue);
            // Variable declaration
            TextView editText = (TextView) findViewById(R.id.suspensionMsg);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(suspensionDate);

            // Process: checking for finite or infinite ban
            if (calendar.get(Calendar.YEAR) == INFINITE_YEAR) { //infinite
                // setting the text
                editText.setText(R.string.landlord_permanent_ban_message);
                editText.setVisibility(View.VISIBLE); //visible

            }
            else { //finite time

                App.getUserHandler().updateLandlordSuspension((Landlord) App.getAppInstance().getUser());

                if (((Landlord) App.getAppInstance().getUser()).getIsSuspended()) { //suspended

                    // var declaration: date formatter
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);

                    // setting the text
                    editText.setText(String.format("%s%s.", getString(R.string.landlord_temp_ban_message) + " ", formatter.format(((Landlord) App.getAppInstance().getUser()).getSuspensionDate())));
                    editText.setVisibility(View.VISIBLE); //visible

                }
                else { //no longer suspended

                    // returning to landlord homescreen
                    Intent intent = new Intent(getApplicationContext(), LandlordScreen.class);
                    startActivity(intent);

                }

            }
        } catch (ParseException e) {
            displayErrorToast("Unable to obtain valid suspension date for Landlord");
            Log.e("showSuspensionMessage", "Unable to parse landlord suspension date: " + e.getMessage());
        }
    }

}
