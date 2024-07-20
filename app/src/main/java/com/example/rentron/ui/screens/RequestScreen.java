package com.example.rentron.ui.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.handlers.RequestHandler;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.data.models.requests.LandlordInfo;
import com.example.rentron.data.models.requests.RequestItem;
import com.example.rentron.ui.screens.search.SearchPropertyItem;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Response;

import java.text.DecimalFormat;
import java.util.Map;

public class RequestScreen extends UIScreen implements StatefulView {

    // Format price to two decimal places
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Store properties and landlord data
    private SearchPropertyItem sPItem;
    private Property propertyData;
    private LandlordInfo landlordData;

    // Key
    public static final String SEARCH_PROPERTY_ITEM_ARG_KEY = "SEARCH_PROPERTY_ITEM_ARG_KEY";

    // Button instantiations
    private ImageButton backButton;
    private Button minusButton;
    private Button plusButton;
    private Button addOrRemoveButton;
    private EditText quantityText;

    // Counter for quantity
    private int totalQuantityCounter = 1;
    // Flag to track if item needs to be added to the cart
    private boolean addToCart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_screen);

        // Buttons for onClick methods
        backButton = findViewById(R.id.button_back);
        minusButton = findViewById(R.id.minus_button);
        plusButton = findViewById(R.id.add_button);
        addOrRemoveButton = findViewById(R.id.add_or_remove_from_cart);
        quantityText = findViewById(R.id.request_quantity);

        // Process: attaching listeners to buttons
        attachOnClickListeners();

        // Get the property and landlord data through intent
        Response response = loadPropertyAndLandlordData();
        if (response.isSuccess()) {
            // Display the property and landlord data
            displayPropertyAndLandlordData();
            // Display quantity
            updateRequestQuantity();
            // Set appropriate text for the button
            setAddOrRemoveButtonText();
        } else {
            // Display error message if unable to load properties data
            displayErrorToast(response.getErrorMessage());
        }
    }

    /**
     * This helper method sets all the on click methods for the buttons of the XML screen
     */
    private void attachOnClickListeners() {
        backButton.setOnClickListener(v -> finish());

        minusButton.setOnClickListener(v -> {
            if (totalQuantityCounter != 1) {
                totalQuantityCounter--;
                updateUI();
            }
        });

        plusButton.setOnClickListener(v -> {
            totalQuantityCounter++;
            updateUI();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.rentron);
        builder.setCancelable(true);
        builder.setTitle("Please confirm your selection");

        addOrRemoveButton.setOnClickListener(v -> {
            if (totalQuantityCounter == 0) {
                displayErrorToast("Please select quantity!");
                return;
            }

            if (Preconditions.isNotNull(App.getClient())) {
                if (addToCart) {
                    if (!App.getClient().getCart().isEmpty()) {
                        Map.Entry<RequestItem, Boolean> entry = App.getClient().getCart().entrySet().iterator().next();
                        RequestItem requestItem = entry.getKey();

                        if (requestItem.getSearchPropertyItem().getLandlord().getLandlordName().equals(sPItem.getLandlord().getLandlordName())) {
                            App.getClient().updateRequestItem(new RequestItem(sPItem, totalQuantityCounter));
                            displaySuccessToast("Item added to cart!");
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            builder.setMessage("This property is offered by a different landlord. Would you like to clear your cart and start a new request?");
                            builder.setPositiveButton("Start new request", (dialog, which) -> {
                                App.getClient().clearCart();
                                App.getClient().updateRequestItem(new RequestItem(sPItem, totalQuantityCounter));
                                displaySuccessToast("Item added to cart!");
                                setResult(Activity.RESULT_OK);
                                finish();
                            });
                            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
                            builder.create().show();
                        }
                    } else {
                        App.getClient().updateRequestItem(new RequestItem(sPItem, totalQuantityCounter));
                        displaySuccessToast("Item added to cart!");
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                } else {
                    App.getClient().updateRequestItem(new RequestItem(sPItem, 0));
                    displaySuccessToast("Item removed from cart!");
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } else {
                displayErrorToast("Unable to update cart!");
            }
        });
    }

    private Response loadPropertyAndLandlordData() {
        try {
            if (getIntent() != null) {
                if (getIntent().getSerializableExtra(SEARCH_PROPERTY_ITEM_ARG_KEY) != null) {
                    this.sPItem = (SearchPropertyItem) getIntent().getSerializableExtra(SEARCH_PROPERTY_ITEM_ARG_KEY);
                    this.propertyData = this.sPItem.getProperty();
                    this.landlordData = this.sPItem.getLandlord();

                    if (App.getClient() != null) {
                        RequestItem requestItem = App.getClient().getRequestItem(propertyData.getPropertyID());
                        if (requestItem != null) {
                            this.totalQuantityCounter = requestItem.getQuantity();
                            this.addToCart = false;
                            plusButton.setClickable(false);
                            minusButton.setClickable(false);
                            updateUI();
                        }
                    }
                    return new Response(true);
                }
            }
        } catch (Exception e) {
            Log.e("RequestScreen", "Unable to get property info");
            displayErrorToast("Unable to display property!");
        }

        return new Response(false, "No valid data available to display");
    }

    @Override
    public void updateUI() {
        updateRequestQuantity();
    }

    @Override
    public void showNextScreen() {
        // Intent to go to the next screen (checkout screen)
    }

    private void displayPropertyAndLandlordData() {
        TextView propertyNameText = findViewById(R.id.request_property_address);
        propertyNameText.setText(this.propertyData.getAddress());

        TextView priceText = findViewById(R.id.request_price_of_property);
        priceText.setText("$ ".concat(df.format(this.propertyData.getPrice())));

        TextView propertyTypeText = findViewById(R.id.request_msg_type);
        propertyTypeText.setText(this.propertyData.getPropertyType());

        TextView amenitiesText = findViewById(R.id.request_msg_amenities);
        String amenitiesString = String.join(", ", this.propertyData.getAmenities());
        amenitiesText.setText(amenitiesString.length() == 0 ? "N/A" : amenitiesString);

        TextView landlordNameText = findViewById(R.id.request_landlord_name_msg);
        landlordNameText.setText(String.valueOf(this.landlordData.getLandlordName()));

        ((TextView) findViewById(R.id.os_landlord_desc)).setText(this.landlordData.getLandlordDescription());
        ((TextView) findViewById(R.id.os_landlord_address)).setText(this.landlordData.getLandlordAddress().toString());
        ((RatingBar) findViewById(R.id.os_landlord_rating)).setRating((float) this.landlordData.getLandlordRating());
    }

    private void updateRequestQuantity() {
        quantityText.setText(String.valueOf(this.totalQuantityCounter), TextView.BufferType.EDITABLE);
    }

    private void setAddOrRemoveButtonText() {
        if (addToCart) {
            addOrRemoveButton.setText("Add to Cart");
        } else {
            addOrRemoveButton.setText("Remove from Cart");
            quantityText.setFocusable(false);
            quantityText.setEnabled(false);
            quantityText.setCursorVisible(false);
            quantityText.setKeyListener(null);
            quantityText.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == RequestHandler.dbOperations.ADD_REQUEST) {
            displaySuccessToast("Successfully added request!");
        } else if (dbOperation == RequestHandler.dbOperations.REMOVE_REQUEST) {
            displaySuccessToast("Successfully removed request!");
        } else if (dbOperation == RequestHandler.dbOperations.UPDATE_REQUEST) {
            displaySuccessToast("Successfully updated request!");
        } else if (dbOperation == RequestHandler.dbOperations.GET_REQUEST_BY_ID) {
            displaySuccessToast("Successfully retrieved request!");
        } else {
            displayErrorToast((String) payload);
        }
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        if (dbOperation == RequestHandler.dbOperations.ADD_REQUEST) {
            displayErrorToast("Failed to add request!");
        } else if (dbOperation == RequestHandler.dbOperations.REMOVE_REQUEST) {
            displayErrorToast("Failed to remove request!");
        } else if (dbOperation == RequestHandler.dbOperations.UPDATE_REQUEST) {
            displayErrorToast("Failed to update request!");
        } else if (dbOperation == RequestHandler.dbOperations.GET_REQUEST_BY_ID) {
            displayErrorToast("Failed to get request!");
        } else {
            displayErrorToast((String) payload);
        }
    }
}
