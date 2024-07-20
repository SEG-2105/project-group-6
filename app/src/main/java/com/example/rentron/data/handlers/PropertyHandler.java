package com.example.rentron.data.handlers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.rentron.app.App;
import com.example.rentron.data.entity_models.PropertyEntityModel;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.ui.screens.search.SearchPropertyItem;
import com.example.rentron.ui.core.StatefulView;
import com.example.rentron.utils.Preconditions;

import java.util.ArrayList;
import java.util.Map;

public class PropertyHandler {

    /**
     * Specify DB operations handled by Property Handler
     */
    public enum dbOperations {
        ADD_PROPERTY,
        ADD_PROPERTY_TO_OFFERED_LIST,
        REMOVE_PROPERTY,
        REMOVE_PROPERTY_FROM_OFFERED_LIST,
        UPDATE_PROPERTY_INFO,
        UPDATE_OFFERED_PROPERTIES,
        GET_MENU,
        GET_PROPERTY_BY_ID,
        ADD_PROPERTY_TO_SEARCH_LIST,
        ADD_PROPERTIES_TO_SEARCH_LIST,
        ERROR
    };

    private StatefulView uiScreen;

    /**
     * Using the Dispatch-Action Pattern to handle actions dispatched to Property Handler
     * @param operationType one of the specified DB operations handled by PropertyHandler
     * @param payload an input data for the handler's operation
     * @param uiScreen instance of the view which needs to know of the operation's success or failure
     */
    public void dispatch(dbOperations operationType, Object payload, StatefulView uiScreen) {

        // guard-clause
        if (Preconditions.isNotNull(uiScreen)) {

            // set the ui screen, so it can be interacted with later on
            this.uiScreen = uiScreen;

            try {
                switch (operationType) {

                    case ADD_PROPERTY:
                        if (Preconditions.isNotNull(payload) && payload instanceof PropertyEntityModel) {
                            // below code might cause exception if validation fails or instance can't be created
                            Property newProperty = new Property((PropertyEntityModel) payload);
                            // if property creation was success, add the property to database
                            App.getPrimaryDatabase().PROPERTIES.addProperty(newProperty);
                        } else {
                            handleActionFailure( operationType, "Invalid Property Object provided");
                        }
                        break;

                    case ADD_PROPERTY_TO_OFFERED_LIST:
                        // update property on remote database first
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            App.getPrimaryDatabase().PROPERTIES.addPropertyToOfferedList((String) payload);
                        } else {
                            handleActionFailure( operationType, "Invalid property ID provided");
                        }
                        break;

                    case REMOVE_PROPERTY:
                        // remove property on remote database first
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            App.getPrimaryDatabase().PROPERTIES.removeProperty((String) payload);
                        } else {
                            handleActionFailure( operationType, "Invalid property ID provided");
                        }
                        break;

                    case REMOVE_PROPERTY_FROM_OFFERED_LIST:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            App.getPrimaryDatabase().PROPERTIES.removePropertyFromOfferedList((String) payload);
                        } else {
                            handleActionFailure( operationType, "Invalid property ID provided" + payload);
                        }
                        break;

                    case UPDATE_OFFERED_PROPERTIES:
                        if (Preconditions.isNotNull(payload) && payload instanceof Map) {
                            updateOfferedProperties((Map<String, Boolean>) payload);
                        } else {
                            handleActionFailure( operationType, "Invalid object provided for map");
                        }
                        break;

                    case GET_MENU:
                        App.getPrimaryDatabase().PROPERTIES.getProperties();
                        break;

                    case GET_PROPERTY_BY_ID:
                        if (Preconditions.isNotNull(payload)) {
                            // if we have been provided with both property id and a landlord id
                            if (payload instanceof String[]) {
                                String[] ids = (String[]) payload;
                                App.getPrimaryDatabase().PROPERTIES.getPropertyById(ids[0], ids[1]);
                            }
                            else {
                                handleActionFailure( operationType, "Invalid arguments provided for getting property by id");
                            }
                        } else {
                            handleActionFailure( operationType, "No arguments provided for getPropertyById");
                        }
                        break;

                    case ADD_PROPERTIES_TO_SEARCH_LIST:
                        // initiate the operation to get all properties
                        App.getPrimaryDatabase().PROPERTIES.getAllProperties();

                    default:
                        Log.e("PropertyHandler dispatch", "Action not implemented yet");
                }
            } catch (Exception e) {
                Log.e("PropertyHandler Dispatch", "Exception: " + e.getMessage());
                uiScreen.dbOperationFailureHandler(dbOperations.ERROR, "Dispatch failed: " + e.getMessage());
            }

        } else {
            Log.e("PropertyHandler Dispatch", "Invalid instance provided for uiScreen");
        }
    }

    /**
     * Method which is called AFTER a successful database operation to make updates locally
     * @param operationType type of database operation which was successful
     * @param payload data for making changes locally
     */
    public void handleActionSuccess(dbOperations operationType, Object payload) {
        // ensure we have a valid uiScreen to inform of success
        if(Preconditions.isNotNull(uiScreen)) {

            try {
                switch (operationType) {

                    case ADD_PROPERTY:
                        if (Preconditions.isNotNull(payload) && payload instanceof Property) {
                            // add property locally
                            ((Landlord) App.getUser()).PROPERTIES.addProperty((Property) payload);
                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Property added successfully!");
                        } else {
                            handleActionFailure(operationType, "Invalid property object provided");
                        }
                        break;

                    case ADD_PROPERTY_TO_OFFERED_LIST:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            // update property locally
                            ((Landlord) App.getUser()).PROPERTIES.addPropertyToOfferedList((String) payload);
                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Property set as offered!");
                        } else {
                            handleActionFailure(operationType, "unable to update property locally as offered, invalid payload");
                        }

                        break;

                    case REMOVE_PROPERTY:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            ((Landlord) App.getUser()).PROPERTIES.removeProperty((String) payload);
                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Property removed successfully!");
                        } else {
                            handleActionFailure(operationType, "Invalid property ID");
                        }
                        break;

                    case REMOVE_PROPERTY_FROM_OFFERED_LIST:
                        if (Preconditions.isNotNull(payload) && payload instanceof String) {
                            ((Landlord) App.getUser()).PROPERTIES.removePropertyFromOfferedList((String) payload);
                            // let UI know about success
                            uiScreen.dbOperationSuccessHandler(operationType, "Property removed from offered list!");
                        } else {
                            handleActionFailure(operationType, "invalid property ID");
                        }
                        break;

                    case UPDATE_PROPERTY_INFO:
                        if (Preconditions.isNotNull(payload) && payload instanceof Property) {
                            // update property locally
                            ((Landlord) App.getUser()).PROPERTIES.updateProperty((Property) payload);
                            uiScreen.dbOperationSuccessHandler(operationType, "updated property info!");
                        } else {
                            handleActionFailure( operationType, "Invalid Property instance provided");
                        }
                        break;

                    case UPDATE_OFFERED_PROPERTIES:
                        uiScreen.dbOperationSuccessHandler(operationType, "updated offered properties list!");
                        break;

                    case GET_MENU:
                        // update the Landlord's properties locally
                        if (Preconditions.isNotNull(payload) && payload instanceof Map) {
                            Map<String, Property> properties = (Map<String, Property>) payload;
                            ((Landlord) App.getUser()).PROPERTIES.setProperties(properties);
                            uiScreen.dbOperationSuccessHandler(operationType, properties);
                        } else {
                            handleActionFailure(operationType, "Invalid payload for getMenu");
                        }
                        break;

                    case GET_PROPERTY_BY_ID:
                        if (Preconditions.isNotNull(payload) && payload instanceof Property) {
                            uiScreen.dbOperationSuccessHandler(operationType, (Property) payload);
                        } else {
                            handleActionFailure(operationType, "Failed to get property by ID");
                        }
                        break;

                    case ADD_PROPERTIES_TO_SEARCH_LIST:
                        // expects an instance of SearchPropertyItem - which contains Property and LandlordInfo
                        if (Preconditions.isNotNull(payload) && payload instanceof ArrayList) {
                            // verify we have valid client instance
                            if (App.getClient() != null) {
                                App.getClient().getSearchProperties().addItems((ArrayList<SearchPropertyItem>) payload);
                            }
                        }
                        break;

                    default:
                        Log.e("handleActionSuccess", "Action not implemented yet");

                }
            } catch (Exception e) {
                Log.e("handleActionSuccess", "Success handler exception: " + e.getMessage());
                uiScreen.dbOperationFailureHandler(dbOperations.ERROR, "Failed to process request");
            }

        } else {
            Log.e("handleActionSuccess", "No UI Screen initialized");
        }
    }

    /**
     * Method which is called AFTER a failure in a database operation to inform UI
     * @param operationType type of database operation which failed
     * @param message a descriptive error message for the developers and analyst (not for client or landlord)
     */
    public void handleActionFailure(dbOperations operationType, String message) {
        // ensure we have a valid uiScreen to inform of failure
        if(Preconditions.isNotNull(uiScreen)) {

            String tag = "handleActionFailure";
            String userMessage = "Failed to process request";

            switch (operationType) {

                case ADD_PROPERTY:
                    tag = "addProperty";
                    userMessage= "Failed to add property!";
                    break;

                case ADD_PROPERTY_TO_OFFERED_LIST:
                    tag = "addingPropertyToOffered";
                    userMessage = "Failed to set property as offered!";
                    break;

                case REMOVE_PROPERTY:
                    tag = "errorRemovingProperty";
                    userMessage = "Failed to remove property!";
                    break;

                case REMOVE_PROPERTY_FROM_OFFERED_LIST:
                    tag = "removePropertyFromOffered";
                    userMessage = "Failed to remove property from offered list!";
                    break;

                case UPDATE_PROPERTY_INFO:
                    tag = "updatePropertyInfo";
                    userMessage = "Failed to update property info!";
                    break;

                case UPDATE_OFFERED_PROPERTIES:
                    tag = "error";
                    userMessage = "Failed to update offered properties!";
                    break;

                case GET_MENU:
                    tag = "errorGetMenu";
                    userMessage = "Failed to get menu!";
                    break;

                case GET_PROPERTY_BY_ID:
                    tag = "errorGettingPropertyById";
                    userMessage = "Failed to get property by id!";
                    break;

                case ADD_PROPERTIES_TO_SEARCH_LIST:
                    tag = "errorGettingSearchList";
                    userMessage = "Unable to retrieve properties for search";

                default:
                    Log.e("handleActionFailure", "Action not implemented yet");
            }

            // send error
            Log.e(tag, message);
            uiScreen.dbOperationFailureHandler(operationType, userMessage);

        } else {
            Log.e("handleActionFailure", "No UI Screen initialized");
        }
    }

    /**
     * Update properties to be offered or un-offered
     * @param data map containing IDs of the properties to be updated
     */
    private void updateOfferedProperties(@NonNull Map<String, Boolean> data) {
        Landlord landlord = (Landlord) App.getUser();
        Map<String, Property> properties = landlord.PROPERTIES.getMenu();
        // guard-clause
        if (Preconditions.isNotNull(data)) {
            for (String propertyId: data.keySet()) {
                // check if property doesn't exists
                if (properties.get(propertyId) == null) {
                    handleActionFailure(dbOperations.UPDATE_OFFERED_PROPERTIES, "Could not find a property for the given property ID");
                    return;
                }

                // check if property needs to be updated
                if (properties.get(propertyId).isOffered() != data.get(propertyId)) {

                    // if property needs to be added to offered list
                    if (data.get(propertyId)) {
                        dispatch(dbOperations.ADD_PROPERTY_TO_OFFERED_LIST, propertyId, uiScreen);
                    } else {
                        dispatch(dbOperations.REMOVE_PROPERTY_FROM_OFFERED_LIST, propertyId, uiScreen);
                    }
                }
            }
            handleActionSuccess(dbOperations.UPDATE_OFFERED_PROPERTIES, null);
        } else {
            handleActionFailure(dbOperations.UPDATE_OFFERED_PROPERTIES, "Invalid data provided to updateOfferedProperties");
        }
    }

}
