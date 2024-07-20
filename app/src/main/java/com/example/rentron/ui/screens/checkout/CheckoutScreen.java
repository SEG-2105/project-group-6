package com.example.rentron.ui.screens.checkout;

import static com.example.rentron.data.handlers.RequestHandler.dbOperations.ADD_REQUEST;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.data.models.requests.RequestItem;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.ui.core.UIScreen;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This screen will use the ListView to populate the screen with all items in the cart using
 * the class CheckoutAdapter for each items information.
 */
public class CheckoutScreen extends UIScreen implements StatefulView {

    // format price to two decimal places
    private static final DecimalFormat df = new DecimalFormat("0.00");

    // buttons
    private ImageButton backButton;
    private Button cancelButton;
    private Button requestButton;

    Map<RequestItem, Boolean> requestData;

    // list to store the items in the cart
    private List<RequestItem> requestItemList;

    // items adapter
    private CheckoutAdapter checkoutAdapter;

    // -------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_screen);

        loadCartData();

        populateCartWithItems();

        // buttons for onClick
        backButton = (ImageButton) findViewById(R.id.button_back3);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        requestButton = (Button) findViewById(R.id.request_button);

        // Process: calling helper method to attach listeners to all buttons
        attachOnClickListeners();

    }

    private void loadCartData() {
        if (App.getClient() != null) {
            // get search property items from app's current instance
            this.requestData = App.getClient().getCart();
        }
        Log.e("cartLoaded", "Loaded properties: " + this.requestData.size());
    }

    private void attachOnClickListeners() {

        // create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Are you sure you wish to continue?");
        // set the icon for the alert dialog
        builder.setIcon(R.drawable.rentron);

        /* onClick logic for checkout button
        you add all the cart request items to the request and call request handler */

        // on click method for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // go to previous screen
        });

        // on click method for request button
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRequestButton();
                if (App.getClient() != null) {
                    App.getClient().clearCart(); //clears cart
                    requestData = App.getClient().getCart(); //sets requestData to equal the empty cart
                }
                showNextScreen(); //finishes action
            }
        });

        // on click method for request button (with Alert Dialog)
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setMessage("You will be clearing your cart and returning to the previous page.");

                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.getClient().clearCart(); //clears cart
                                requestData = App.getClient().getCart(); //sets requestData to equal the empty cart
                                finish(); //finishes action
                                Log.e("cartClear", "Clear the list: " + requestData.size());
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void populateCartWithItems() {
        // initialize requestItemList as empty list
        this.requestItemList = new ArrayList<>();
        // get the list view component
        ListView requestList = findViewById(R.id.items_in_cart);
        // get the adapter
        this.checkoutAdapter = new CheckoutAdapter(this, R.layout.activity_checkout_screen, this.requestItemList);
        // attach adapter to list view
        requestList.setAdapter(this.checkoutAdapter);
        // add data to the adapter
        for (RequestItem sMItemId : this.requestData.keySet()) {
            this.requestItemList.add(sMItemId);
        }
        // if we do have items in cart
        if (!this.requestItemList.isEmpty()) {
            /// make the request submission container visible
            LinearLayout requestSubmissionContainer = findViewById(R.id.requestSubmissionContainer);
            requestSubmissionContainer.setVisibility(View.VISIBLE);
            // display total cost
            TextView totalCostAmount = findViewById(R.id.totalCostAmount);
            totalCostAmount.setText(getTotalCost());
            // display credit card info
            displayCreditCardInfo();
        } else {
            // make the request submission container invisible
            LinearLayout requestSubmissionContainer = findViewById(R.id.requestSubmissionContainer);
            requestSubmissionContainer.setVisibility(View.GONE);
        }
        Log.e("cartPopulation", "Populated the list: " + this.requestItemList.size());
    }

    private void handleRequestButton() {
        App.REQUEST_HANDLER.dispatch(ADD_REQUEST, null, this);
    }

    private String getTotalCost() {
        if (this.requestItemList != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                df.setRoundingMode(RoundingMode.UP);
                return df.format(requestItemList.stream().mapToDouble(el -> el.getSearchPropertyItem().getProperty().getPrice() * el.getQuantity()).sum());
            }
        }
        // if no request items
        return String.valueOf(0.00);
    }

    private void displayCreditCardInfo() {
        // make sure we have a valid client
        if (App.getClient() != null) {
            TextView clientCreditCard = findViewById(R.id.clientCreditCardInfo);
            String creditCardNumber = App.getClient().getClientCreditCard().getNumber();
            String creditCardHashed = "XXXX-XXXX-XXXX-" + creditCardNumber.substring(creditCardNumber.length() - 4);
            clientCreditCard.setText(creditCardHashed);
        }
    }

    @Override
    public void updateUI() {
        // you update the UI when the client decides to remove a request item from the cart
        // so first you'd call request handler,
        // then you'd update the UI if the removal is successful
    }

    @Override
    public void showNextScreen() {
        // this should be called when the final submit button is clicked
        // this should finish the activity

        this.finish(); //returning to client's home screen
    }

    @Override
    public void dbOperationSuccessHandler(Object dbOperation, Object payload) {
        if (dbOperation == ADD_REQUEST) {
            displaySuccessToast((String) payload);
        }
    }

    @Override
    public void dbOperationFailureHandler(Object dbOperation, Object payload) {
        // display the error message
        if (payload instanceof String) {
            displayErrorToast((String) payload);
        }
    }

}
