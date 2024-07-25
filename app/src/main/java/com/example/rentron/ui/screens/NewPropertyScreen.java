package com.example.rentron.ui.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.widget.SwitchCompat; // Correct import

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.PropertyEntityModel;
import com.example.rentron.data.handlers.PropertyHandler;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
//import androidx.appcompat.widget.SwitchCompat;


import java.util.ArrayList;

public class NewPropertyScreen extends UIScreen implements StatefulView {

    // Variable Declaration
    protected ArrayList<String> amenities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property_screen);

        /*
        Add information to spinner for property type
         */
        Spinner spinner = findViewById(R.id.property_type);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.property_types_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // attach onClick handlers to buttons
        attachOnClickListeners();
    }

    /**
     * this method attaches the click methods to all the buttons
     */
    private void attachOnClickListeners() {

        // Variable Declaration
        Button addPropertyButton = findViewById(R.id.add_property_button);
        Button backButton = findViewById(R.id.back_button);

        // Process: setting the new onClick method for addProperty
        addPropertyButton.setOnClickListener(view -> onAddProperty(view)); // calling helper method to add the property

        // Process: setting the new onClick method for the back button
        backButton.setOnClickListener(view -> {
            cancelAddingProperty(); // stopping the activity
            showNextScreen(); // returning to the previous screen
        });
    }

    // Helper Methods for Adding the Property------------------------------------------------------------------------
    /**
     * this method adds the selected amenities to the amenities list
     */
    private void addAmenitiesToList() {

        // Variable Declaration
        CheckBox currentCheck = findViewById(R.id.pool_checkbox);

        // Process: adding amenities to list
        if (currentCheck.isChecked()) { // pool
            amenities.add("Pool"); // adding to amenities array
        }

        // updating checkbox
        currentCheck = findViewById(R.id.gym_checkbox);

        if (currentCheck.isChecked()) { // gym
            amenities.add("Gym"); // adding to amenities array
        }

        // updating checkbox
        currentCheck = findViewById(R.id.garden_checkbox);
        if (currentCheck.isChecked()) { // garden
            amenities.add("Garden");  // adding to amenities String
        }
    }

    /**
     * this method creates the new property and adds it to the landlord's properties
     * @param view
     *  the current view selected
     */
    public void onAddProperty(View view) {

        addAmenitiesToList(); // calling helper method to add all amenities

        // Variable Declaration
        String landlordID = App.getUserId();

        EditText propertyAddress = findViewById(R.id.property_address);
        EditText rooms = findViewById(R.id.rooms);
        Spinner propertyType = findViewById(R.id.property_type);
        EditText bathrooms = findViewById(R.id.bathrooms);
        EditText floors = findViewById(R.id.floors);
        androidx.appcompat.widget.SwitchCompat laundry = findViewById(R.id.laundry);
        EditText parking = findViewById(R.id.parking);
        EditText price = findViewById(R.id.price);
        androidx.appcompat.widget.SwitchCompat offered = findViewById(R.id.offer_property_switch);

        double priceValue;

        // Process: validating the price
        try {
            // Initialization
            priceValue = Double.parseDouble(price.getText().toString());

            // Variable Declaration
            PropertyEntityModel propertyEntityModel = new PropertyEntityModel(null, landlordID,
                    propertyAddress.getText().toString(),
                    Integer.parseInt(rooms.getText().toString()),
                    propertyType.getSelectedItem().toString(),
                    Integer.parseInt(bathrooms.getText().toString()),
                    amenities,
                    Integer.parseInt(floors.getText().toString()),
                    laundry.isChecked(),
                    Integer.parseInt(parking.getText().toString()),
                    offered.isChecked(),
                    priceValue);

            App.PROPERTY_HANDLER.dispatch(PropertyHandler.dbOperations.ADD_PROPERTY, propertyEntityModel, this); // calling add property method from propertyhandler

        } catch (NumberFormatException e) {
            // error toast here
            displayErrorToast("Invalid price");
        }
    }

    /**
     * this helper method cancels the add property action and ends the activity
     */
    private void cancelAddingProperty() {
        // finish the activity and return
        this.setResult(Activity.RESULT_CANCELED);
        this.finish();
    }

    // UI Methods-----------------------------------------------------------------------------------------------
    @Override
    public void updateUI() {}

    /**
     * this method returns the landlord to the main screen
     */
    @Override
    public void showNextScreen() {

        // Variable Declaration
        Intent intent = new Intent(getApplicationContext(), LandlordScreen.class);

        // Process: starting new intent
        startActivity(intent);
    }

    // Firebase Methods------------------------------------------------------------------------------------------
    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == PropertyHandler.dbOperations.ADD_PROPERTY) {
            // adding new property completed
            displaySuccessToast((String) payload);
            // finish the activity and return
            this.setResult(Activity.RESULT_OK);
            this.finish(); // returning to landlord's main screen
        }
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        if (dbOperation == PropertyHandler.dbOperations.ADD_PROPERTY) {
            // failed adding a new property
            displayErrorToast("Failed to add property!");
        } else {
            displayErrorToast((String) payload);
        }
    }
}
