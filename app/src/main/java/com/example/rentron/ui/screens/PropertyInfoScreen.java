package com.example.rentron.ui.screens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.PropertyEntityModel;
import com.example.rentron.data.handlers.PropertyHandler;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PropertyInfoScreen extends UIScreen implements StatefulView {

    // format price to two decimal places
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // create a new object of type property that contains the respective property's information/data
    PropertyEntityModel propertyData;

    // button variables
    ImageButton backButton;
    Button offeringButton;
    Button removeButton;

    // key to pass property's information through intent
    public final static String PROPERTY_DATA_ARG_KEY = "PROPERTY_DATA_ARG_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_info_screen);

        // buttons for onClick methods
        backButton = findViewById(R.id.back_btn);
        offeringButton = findViewById(R.id.offering_btn);
        removeButton = findViewById(R.id.remove_btn);

        // gets property data from previous screen
        try {
            propertyData = (PropertyEntityModel) getIntent().getSerializableExtra(PROPERTY_DATA_ARG_KEY);
            updateUI();
        } catch (Exception e) {
            Log.e("PropertyInfoScreen", "Unable to create property object :(", e);
            displayErrorToast("Unable to retrieve the property info!");
        }

        // on click method for back button
        backButton.setOnClickListener(v -> finish());

        // alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // confirm their click to change offering
        builder.setCancelable(true);
        builder.setTitle("Please confirm your selection");
        // set the icon for the alert dialog
        builder.setIcon(R.drawable.rentron);

        // on click method for changing the offering value of the property
        offeringButton.setOnClickListener(v -> {
            // change text
            if (propertyData.isOffered()) { // currently being offered
                builder.setMessage("You will be unoffering this property now!");
            } else { // currently is not offered
                builder.setMessage("You will be offering this property now!");
            }

            builder.setPositiveButton("Confirm",
                    (dialog, which) -> {
                        offeredButtonClickHandler();
                        showRemoveButton();
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> { });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        // on click method for removing a property (only if it is not currently offered)
        removeButton.setOnClickListener(v -> {
            if (propertyData.isOffered()) { // currently being offered
                displayErrorToast("CANNOT REMOVE AN OFFERED PROPERTY");
            } else { // currently is not offered
                // change text
                builder.setMessage("Are you sure you want to remove this property? \nThis cannot be changed.");

                builder.setPositiveButton("Confirm",
                        (dialog, which) -> removeButtonClickHandler());
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> { });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        showRemoveButton();
    }

    /**
     * Confirms the action of the user to unoffer/offer the property from the Action Dialog Pop-up
     */
    private void offeredButtonClickHandler() {
        if (propertyData.isOffered()) { // currently offered
            Log.e("Property ID", "" + propertyData.getPropertyID());
            App.PROPERTY_HANDLER.dispatch(PropertyHandler.dbOperations.REMOVE_PROPERTY_FROM_OFFERED_LIST, propertyData.getPropertyID(), this);
            propertyData.setOffered(false);
            updateUI();
        } else { // currently not offered
            App.PROPERTY_HANDLER.dispatch(PropertyHandler.dbOperations.ADD_PROPERTY_TO_OFFERED_LIST, propertyData.getPropertyID(), this); // is now offering
            propertyData.setOffered(true);
            updateUI();
        }
    }

    /**
     * Helper method that will remove a button from menu
     */
    private void removeButtonClickHandler() {
        App.PROPERTY_HANDLER.dispatch(PropertyHandler.dbOperations.REMOVE_PROPERTY, propertyData.getPropertyID(), this);
    }

    /**
     * Helper method that toggles remove button's visibility on if the property is offered or not
     */
    public void showRemoveButton() {
        View button = findViewById(R.id.remove_btn);
        if (propertyData.isOffered()) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
        }
    }

    // updates the screen with the information according to the property that was clicked on the previous screen
    @Override
    public void updateUI() {
        try {
            updatePropertyInfoScreen(propertyData.getAddress(), propertyData.getPrice(), propertyData.getPropertyType(), propertyData.getRooms(),
                    propertyData.getAmenities(), propertyData.getBathrooms(), propertyData.getFloors(), propertyData.isLaundry(), propertyData.getParking(), propertyData.isOffered());
        } catch (Exception e) {
            Log.e("PropertyInfoScreen", "Unable to create property object :(", e);
            displayErrorToast("Unable to retrieve the property info!");
        }
    }

    @Override
    public void showNextScreen() {
        // Implementation for showing the next screen if needed
    }

    /**
     * Updates the text on the property info screen to its respective information
     * @param address
     * @param price
     * @param propertyType
     * @param rooms
     * @param amenities
     * @param bathrooms
     * @param floors
     * @param laundry
     * @param parking
     * @param offered
     */
    public void updatePropertyInfoScreen(String address, double price, String propertyType, int rooms,
                                         ArrayList<String> amenities, int bathrooms, int floors, boolean laundry, int parking, boolean offered) {
        // sets the text for the property address
        TextView addressText = findViewById(R.id.address_of_property);
        addressText.setText(address);

        // sets the text for price
        TextView priceText = findViewById(R.id.price_of_property);
        priceText.setText("$ " + df.format(price));

        // sets the text for the property type
        TextView propertyTypeText = findViewById(R.id.type_of_property);
        propertyTypeText.setText(propertyType);

        // sets the text for rooms
        TextView roomsText = findViewById(R.id.rooms);
        roomsText.setText(String.valueOf(rooms));

        // sets the text for amenities
        TextView amenitiesText = findViewById(R.id.amenities);
        amenitiesText.setText(String.join(", ", amenities));

        // sets the text for bathrooms
        TextView bathroomsText = findViewById(R.id.bathrooms);
        bathroomsText.setText(String.valueOf(bathrooms));

        // sets the text for floors
        TextView floorsText = findViewById(R.id.floors);
        floorsText.setText(String.valueOf(floors));

        // sets the text for laundry
        TextView laundryText = findViewById(R.id.laundry);
        laundryText.setText(laundry ? "Yes" : "No");

        // sets the text for parking
        TextView parkingText = findViewById(R.id.parking);
        parkingText.setText(String.valueOf(parking));

        // set the text for offering button
        Button offeringButton = findViewById(R.id.offering_btn);

        if (offered) { // property is currently being offered
            offeringButton.setText("Unoffer Property");
        } else { // property is currently not being offered
            offeringButton.setText("Offer Property");
        }
    }

    // Firebase Methods-----------------------------------------------------------------------------------------------
    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        // if any DB operation is initiated on a property, and it's a success, we update properties list to show current changes
        App.getAppInstance().getPropertiesListScreen().notifyDataChanged();
        // display success message
        displaySuccessToast((String) payload);

        // if operation was to delete the property, close the property info screen
        if (dbOperation == PropertyHandler.dbOperations.REMOVE_PROPERTY) {
            finish();
        }
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        // display error message
        displayErrorToast((String) payload);
    }
}
