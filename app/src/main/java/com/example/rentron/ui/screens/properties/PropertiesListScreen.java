package com.example.rentron.ui.screens.properties;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.rentron.R;
import com.example.rentron.app.AppInstance;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.ui.core.UIScreen;

import java.util.ArrayList;
import java.util.List;

public class PropertiesListScreen extends UIScreen {

    // list to store properties data
    private List<Property> propertiesData;
    private List<Property> properties;
    // key to specify type of properties data being displayed
    public final static String PROPERTIES_TYPE_ARG_KEY = "PROPERTIES_TYPE_ARG_KEY";
    // key to provide custom list of properties through intent
    public final static String PROPERTIES_DATA_ARG_KEY = "PROPERTIES_DATA_ARG_KEY";

    // Defining an enum to describe type of properties data this view can display
    public enum PROPERTIES_TYPE {
        PROPERTIES,
        OFFERED_PROPERTIES,
        CUSTOM
    }
    // store type of properties being displayed
    private PROPERTIES_TYPE properties_type;
    // back button
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_list_screen);

        // check the properties data to be loaded
        loadPropertiesData();

        // initialize view data holder
        properties = new ArrayList<>();

        // populate properties listView
        populatePropertiesList();

        AppInstance.getAppInstance().setPropertiesListScreen(this);

        // buttons for onClick methods
        backButton = (ImageButton) findViewById(R.id.back_btn2);

        // on click method for back button
        backButton.setOnClickListener(v -> finish());
    }

    private void loadPropertiesData() {
        // check if have intent data and there is a property specified for property type
        if (getIntent() != null && getIntent().getStringExtra(PROPERTIES_TYPE_ARG_KEY) != null) {

            String propertyType = getIntent().getStringExtra(PROPERTIES_TYPE_ARG_KEY);

            // load offered properties for a logged in Landlord
            if (propertyType.equals(PROPERTIES_TYPE.OFFERED_PROPERTIES.toString())) {
                properties_type = PROPERTIES_TYPE.OFFERED_PROPERTIES;
                loadLoggedInLandlordsOfferedProperties();
            }

            // load custom properties provided in the intent
            else if (propertyType.equals(PROPERTIES_TYPE.CUSTOM.toString())) {
                properties_type = PROPERTIES_TYPE.CUSTOM;
                retrieveCustomPropertiesData();
            }

            // load all properties of logged in landlord
            else {
                properties_type = PROPERTIES_TYPE.PROPERTIES;
                loadLoggedInLandlordsProperties();
            }
        } else {
            // if nothing provided in intent, display all properties by default if landlord is logged in
            properties_type = PROPERTIES_TYPE.PROPERTIES;
            loadLoggedInLandlordsProperties();
        }
    }

    private void retrieveCustomPropertiesData() {
        // get list of properties from the intent
        try {
            // check if we have properties
            if (getIntent().getSerializableExtra(PROPERTIES_DATA_ARG_KEY) != null) {
                // retrieve list of properties (if throws exception, means data is invalid and we handle it)
                this.propertiesData = (List<Property>) getIntent().getSerializableExtra(PROPERTIES_DATA_ARG_KEY);
            } else {
                loadLoggedInLandlordsProperties();
            }
        } catch (Exception e) {
            Log.e("PropertiesListScreen", "An exception occurred: " + e.getMessage());
            displayErrorToast("Unable to load properties");
        }
    }

    private void loadLoggedInLandlordsProperties() {
        // if no list of properties was provided, just display all properties of the Landlord if a landlord is logged in
        if (AppInstance.getAppInstance().getUser() instanceof Landlord) {
            // set properties data to be the list of properties of the Landlord
            this.propertiesData = ((Landlord) AppInstance.getAppInstance().getUser()).PROPERTIES.getListOfProperties();
        } else {
            Log.e("PropertiesListScreen", "No properties provided in intent, and no landlord logged in");
            displayErrorToast("No properties available to be displayed");
        }
    }

    private void loadLoggedInLandlordsOfferedProperties() {
        // check if the current logged in user is a Landlord
        if (AppInstance.getAppInstance().getUser() instanceof Landlord) {
            // set properties data to be the list of properties of the Landlord
            this.propertiesData = ((Landlord) AppInstance.getAppInstance().getUser()).PROPERTIES.getListOfOfferedProperties();
        } else {
            Log.e("PropertiesListScreen", "Can't show offered property. Current logged in user is not a Landlord");
            displayErrorToast("No offered properties available to be displayed");
        }
    }

    private void populatePropertiesList() {
        this.properties = new ArrayList<>();
        // get the properties list
        ListView propertiesList = findViewById(R.id.plPropertiesList);
        // get the adapter
        // properties adapter
        PropertiesAdapter propertiesAdapter = new PropertiesAdapter(this, R.layout.activity_properties_list_item, this.properties);
        // Attach the adapter to the properties listView
        propertiesList.setAdapter(propertiesAdapter);
        // add data to the adapter
        for (Property property: this.propertiesData) {
            propertiesAdapter.add(property);
        }
    }

    public void notifyDataChanged() {
        // reload properties data
        if (properties_type == PROPERTIES_TYPE.OFFERED_PROPERTIES) {
            loadLoggedInLandlordsOfferedProperties();
        } else if (properties_type == PROPERTIES_TYPE.CUSTOM) {
            retrieveCustomPropertiesData();
        } else {
            loadLoggedInLandlordsProperties();
        }
        // re-populate properties list
        populatePropertiesList();
    }
}
