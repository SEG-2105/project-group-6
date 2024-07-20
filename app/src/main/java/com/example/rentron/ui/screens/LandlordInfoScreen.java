package com.example.rentron.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Address;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;

public class LandlordInfoScreen extends UIScreen implements StatefulView {

    ImageButton landlordInfoBackBtn;
    Landlord landlordData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_info_screen);

        // buttons for onClick methods
        landlordInfoBackBtn = (ImageButton) findViewById(R.id.button_back5);

        // on click method for back button
        landlordInfoBackBtn.setOnClickListener(v -> finish());

        // Try getting landlord's data from previous screen
        try {
            landlordData = App.getLandlord();
            updateUI();
        } catch (Exception e) {
            Log.e("LandlordInfoScreen", "Unable to retrieve the landlord info!");
            displayErrorToast("Unable to retrieve the landlord info!");
        }

        // attach onClick listeners
        attachOnClickListeners();
    }

    private void attachOnClickListeners(){
        // on click method for back button
        landlordInfoBackBtn.setOnClickListener(v -> finish());
    }

    @Override
    public void updateUI() {
        try {
            updateLandlordInfoScreen(
                    landlordData.getFirstName() + " " + landlordData.getLastName(),
                    landlordData.getEmail(),
                    landlordData.getAddress(),
                    landlordData.getLandlordRating(),
                    landlordData.getDescription(),
                    landlordData.getIsSuspended(),
                    landlordData.getNumOfRequestsSold()
            );
        } catch (Exception e) {
            Log.e("LandlordInfoScreen", "Unable to create landlord object :(");
            displayErrorToast("Unable to retrieve the landlord info!");
        }
    }

    @Override
    public void showNextScreen() {
        // Implementation for showing next screen if needed
    }

    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        // Implementation for handling successful DB operations if needed
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        // Implementation for handling failed DB operations if needed
    }

    /**
     * Updates the text on the landlord info screen to its respective information
     * @param name
     * @param emailAddress
     * @param location
     * @param landlordRating
     * @param description
     * @param isSuspended
     * @param totalRequestsServed
     */
    public void updateLandlordInfoScreen(String name, String emailAddress, Address location, double landlordRating,
                                         String description, boolean isSuspended, int totalRequestsServed) {
        // Setting the text for the landlord's information
        TextView landlordNameText = (TextView) findViewById(R.id.landlord_name_msg);
        landlordNameText.setText(name);

        // sets the text for landlord's email
        TextView emailText = (TextView) findViewById(R.id.landlord_email_msg);
        emailText.setText(emailAddress);

        // sets the text for the location
        TextView locationText = (TextView) findViewById(R.id.landlord_location_msg);
        locationText.setText(location.toString());

        // sets the text for landlord's rating
        RatingBar ratingBar = (RatingBar) findViewById(R.id.landlord_rating_msg);
        ratingBar.setRating((float) landlordRating);
        ratingBar.setIsIndicator(true);

        // sets the text for the description
        TextView descriptionText = (TextView) findViewById(R.id.landlord_description_msg);
        descriptionText.setText(description);

        // sets the text for requests served
        TextView requestsServedText = (TextView) findViewById(R.id.landlord_propertiesSold_msg);
        requestsServedText.setText(String.valueOf(totalRequestsServed));
    }
}
