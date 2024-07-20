package com.example.rentron.ui.screens.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rentron.R;
import com.example.rentron.app.App;
import com.example.rentron.ui.core.UIScreen;
import com.example.rentron.ui.screens.checkout.CheckoutScreen;
import com.example.rentron.utils.PostalCodeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SearchScreen extends UIScreen {

    // map to load search property items
    Map<String, SearchPropertyItem> sMItemsData;
    // list used by SearchPropertyItemsAdapter to populate SearchPropertyItems on the ListView
    List<SearchPropertyItem> sMItems;

    ImageButton backButton;
    //ImageButton searchButton;
    ImageButton checkoutButton;

    ListView sMList;
    EditText searchBox;
    TextView noSearchResultMessage;

    // adapter to handle list view
    private SearchPropertyItemsAdapter sMItemsAdapter;

    // instantiate a Postal Code comparator to sort search results by
    // closeness to Client's postal code
    PostalCodeComparator postalCodeComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property_screen);

        backButton = findViewById(R.id.back_btn3);
        //searchButton = findViewById(R.id.searchBtn); //add when screen is done
        checkoutButton = findViewById(R.id.cartBtn);

        // get the list view component
        sMList = findViewById(R.id.smPropertiesList);
        searchBox = findViewById(R.id.searchBox);
        noSearchResultMessage = findViewById(R.id.noSearchResultMessage);

        attachOnClickListeners();

        // load the search property data
        loadSearchPropertyData();
        // populate the list view
        populateListView();
        // subscribe to SearchProperties for data updates
        subscribeToDataChanges();

        try {
            postalCodeComparator = new PostalCodeComparator(App.getClient().getAddress().getPostalCode());
        } catch (Exception e) {
            Log.e("searchProperties", "Unable to create instance of postal code comparator: " + e.getMessage());
            displayErrorToast("Unable to sort results by closeness to client");
        }

        // CAUTION: uncomment below only when keywords needs to be re-generated for all properties in database
        // App.getPrimaryDatabase().PROPERTIES.generatePropertyKeywords();
    }

    public void attachOnClickListeners() {
        backButton.setOnClickListener(view -> {
            // close the activity
            setResult(Activity.RESULT_OK);
            finish();
        });

        checkoutButton.setOnClickListener(view -> {
            if (App.getClient().getCart().size() == 0)
                displayErrorToast("No items in cart!");
            else {
                Intent intent = new Intent(getApplicationContext(), CheckoutScreen.class);
                startActivity(intent);
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if nothing in search box
                if (s.length() != 0) {
                    // display search results based on the query entered by client
                    displaySearchResult(s.toString());
                } else {
                    // display all properties
                    populateListView();
                }
                Log.e("searchR", "Search box text changed: " + s);
            }
        });
    }

    public void newSearchItemsAdded(List<SearchPropertyItem> newItems) {
        // update our local data store
        loadSearchPropertyData();
        // add all new items
        this.sMItems.addAll(newItems);
        // inform adapter of the change
        sMItemsAdapter.notifyDataSetChanged();
        // TODO: below line for test, remove later
        Log.e("searchProperties", "new search items added, sM: " + this.sMItems.size() + " sM D: " + this.sMItemsData.size());
    }

    /**
     * Load search property data from app
     */
    private void loadSearchPropertyData() {
        // ensure we have a valid client logged in (only client's search properties)
        if (App.getClient() != null) {
            // get search property items from app's current instance
            this.sMItemsData = App.getClient().getSearchProperties().getSearchPropertyItems();
        }
        Log.e("searchProperties", "Loaded properties: " + this.sMItemsData.size());
    }

    /**
     * Use the adapter to update the items being displayed in list view
     */
    private void populateListView() {
        // initialize smItems as empty list
        this.sMItems = new ArrayList<>();
        // get the adapter
        this.sMItemsAdapter = new SearchPropertyItemsAdapter(this, R.layout.activity_search_property_item, this.sMItems);
        // attach adapter to list view
        sMList.setAdapter(this.sMItemsAdapter);
        // add data to the adapter
        for (String sMItemId : this.sMItemsData.keySet()) {
            this.sMItems.add(this.sMItemsData.get(sMItemId));
        }
        Log.e("searchProperties", "Populated the list: " + this.sMItems.size());
    }

    /**
     * Subscribe to data changes for search property items
     */
    private void subscribeToDataChanges() {
        // ensure we have a valid client logged in
        if (App.getClient() != null) {
            App.getClient().getSearchProperties().subscribeToDataChanges(this);
        }
    }

    private void displaySearchResult(String query) {
        if (App.getClient() != null) {
            // get the list of SearchPropertyItems matching the query entered in search box
            List<SearchPropertyItem> searchResult = App.getClient().getSearchProperties().searchPropertyItems(query);

            // if there are no matching results, display a message indicating so and return
            if (searchResult.isEmpty()) {
                setNoSearchResultMessageVisibility(true);
                return;
            } else {
                // hide the no search result message and continue processing
                setNoSearchResultMessageVisibility(false);
            }

            // sort the search results by closeness to client (based on postal codes)
            if (postalCodeComparator != null) {
                Collections.sort(searchResult, (sR1, sR2) -> postalCodeComparator.comparePostalCodes(sR1.getLandlord().getLandlordAddress().getPostalCode(), sR2.getLandlord().getLandlordAddress().getPostalCode()));
                Log.e("searchProperties", "search results sorted");
            }
            // clear current items in sMItems
            this.sMItems = new ArrayList<>();
            // get the adapter
            this.sMItemsAdapter = new SearchPropertyItemsAdapter(this, R.layout.activity_search_property_item, this.sMItems);
            // attach adapter to list view
            sMList.setAdapter(this.sMItemsAdapter);
            // load the result properties
            this.sMItems.addAll(searchResult);
        }
    }

    private void setNoSearchResultMessageVisibility(boolean visible) {
        if (visible) {
            // clear the list items, and display no result
            sMItemsAdapter.clear();
            noSearchResultMessage.setVisibility(View.VISIBLE);
        } else {
            noSearchResultMessage.setVisibility(View.GONE);
        }
    }
}
