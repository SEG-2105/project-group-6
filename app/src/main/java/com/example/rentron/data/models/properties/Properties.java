package com.example.rentron.data.models.properties;

import androidx.annotation.NonNull;

import com.example.rentron.utils.Preconditions;
import com.example.rentron.utils.Response;
import com.example.rentron.utils.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Properties {

    // Store Properties in a Map<PropertyID, Property> key-value pairs
    private Map<String, Property> properties;

    /**
     * Default constructor initializes a HashMap for storing Properties
     */
    public Properties() {
        this.properties = new HashMap<>(); //<PropertyID, Property> key-value pair
    }

    /**
     * Retrieve a property from all properties of the Landlord by property ID
     * @param propertyID representing the ID of the property
     * @return a Result object containing the property is successful in getting the associated property, else error message
     */
    public Result<Property, String> getProperty(@NonNull String propertyID) {
        // guard-clause
        if (Preconditions.isNotEmptyString(propertyID)) {
            // check if property exists
            if (this.properties.get(propertyID) != null) {
                return new Result<>(this.properties.get(propertyID), null);
            } else {
                return new Result<>(null, "Could not find any property for the provided property ID");
            }
        } else {
            return new Result<>(null, "Invalid property ID provided");
        }
    }

    public void setProperties(@NonNull Map<String, Property> propertiesData) {
        this.properties = propertiesData;
    }

    /**
     * Add a new property to the landlord's list of properties (menu)
     * @param newProperty Property to be added
     */
    public Response addProperty(@NonNull Property newProperty) {
        // guard-clause
        // property must have a valid id which will be used as a key
        if (Preconditions.isNotEmptyString(newProperty.getPropertyID())) {
            // check if property already exists
            if (this.properties.get(newProperty.getPropertyID()) != null) {
                return new Response(false, "Property with same ID already exists! Use updateProperty to update an existing property");
            }
            // add the new property
            this.properties.put(newProperty.getPropertyID(), newProperty);
            // return success
            return new Response(true);
        } else {
            return new Response(false, "Property does not have a valid ID");
        }
    }

    /**
     * Add a property to the landlord's offered property
     * @param propertyId ID of the property that needs to be added to the menu
     * @return Response object indicating success, or failure (with an error message)
     */
    public Response addPropertyToOfferedList(@NonNull String propertyId) {
        // guard-clause
        if (Preconditions.isNotEmptyString(propertyId)) {
            // check if property exists and has a valid object
            if (this.properties.get(propertyId) != null) {
                // add property to the menu
                this.properties.get(propertyId).setOffered(true);
                // return success
                return new Response(true);
            } else {
                return new Response(false, "Could not find the any property for the provided ID");
            }
        } else {
            return new Response(false, "Property does not have a valid ID");
        }
    }

    /**
     * Remove a property from the list of all properties added by a Landlord
     * @param propertyId ID of the property to be removed
     */
    public Response removeProperty(@NonNull String propertyId) {
        // guard-clause
        if (Preconditions.isNotEmptyString(propertyId)) {
            // check if property exists
            if (this.properties.get(propertyId) != null) {
                // remove the property
                this.properties.remove(propertyId);
                // return operation success
                return new Response(true);
            } else {
                return new Response(false, "Could not find any property for the provided property ID");
            }
        } else {
            return new Response(false, "Invalid property ID provided");
        }
    }

    /**
     * Remove a property from Landlord's offered properties
     * @param propertyId ID of the property to be removed
     */
    public Response removePropertyFromOfferedList(@NonNull String propertyId) {
        // guard-clause
        if (Preconditions.isNotEmptyString(propertyId)) {
            // check if property exists
            if (this.properties.get(propertyId) != null) {
                // remove property from menu
                this.properties.get(propertyId).setOffered(false);
                // return operation success
                return new Response(true);
            } else {
                return new Response(false, "Could not find any property for the provided property ID");
            }
        } else {
            return new Response(false, "Invalid property ID provided");
        }
    }

    /**
     * Update an existing property
     * @param property property instance, must have a valid property ID
     * @return Response indicating operation success or failure
     */
    public Response updateProperty(@NonNull Property property) {
        // guard-clause
        // property must have a valid id which will be used as a key
        if (Preconditions.isNotEmptyString(property.getPropertyID())) {
            // check if property doesn't exists
            if (!this.properties.containsKey(property.getPropertyID())) {
                return new Response(false, "Could not find a property for the given property ID");
            }
            // update the property
            this.properties.put(property.getPropertyID(), property);
            // return success
            return new Response(true);
        } else {
            return new Response(false, "Property does not have a valid ID");
        }
    }

    /**
     * Method to retrieve a map object containing properties which are currently being offered by Landlord
     * @return a Map containing Property ID's as keys and Property objects as values
     */
    public Map<String, Property> getOfferedProperties() {
        // map to store the result
        HashMap<String, Property> offeredProperties = new HashMap<>();
        // filter and add offered properties to above map
        for (Property property : this.properties.values()) {
            if (property.isOffered()) {
                offeredProperties.put(property.getPropertyID(), property);
            }
        }
        // return the result
        return offeredProperties;
    }

    /**
     * Method to retrieve a map object containing all properties added by the Landlord
     * @return a Map containing Property ID's as keys and Property objects as values
     */
    public Map<String, Property> getMenu() {
        return this.properties;
    }

    /**
     * Method to retrieve a list containing all properties added by the Landlord
     * @return a List containing Property objects
     */
    public List<Property> getListOfProperties() {
        return new ArrayList<>(this.properties.values());
    }

    /**
     * Method to retrieve a list containing properties currently offered by the Landlord
     * @return a List containing Property objects
     */
    public List<Property> getListOfOfferedProperties() {
        return new ArrayList<>(getOfferedProperties().values());
    }
}
