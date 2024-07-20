package com.example.rentron.app;

import android.util.Log;

import com.example.rentron.data.handlers.DataHandlers;
import com.example.rentron.data.models.User;
import com.example.rentron.data.models.UserRoles;
import com.example.rentron.data.models.inbox.PropertyManagerInbox;
import com.example.rentron.data.sources.FirebaseRepository;
import com.example.rentron.ui.screens.LandlordScreen;
import com.example.rentron.ui.screens.completed_requests.CompletedRequestsScreen;
import com.example.rentron.ui.screens.pending_requests.PendingRequestsScreen;
import com.example.rentron.ui.screens.properties.PropertiesListScreen;
import com.example.rentron.utils.Preconditions;
import com.google.firebase.auth.FirebaseAuth;

public class AppInstance {

    // singleton instance
    private static AppInstance appInstance;

    private FirebaseRepository primaryDatabase;
    private DataHandlers appDataHandlers;
    private User user;
    private PropertyManagerInbox propertyManagerInbox;
    private PropertiesListScreen propertiesListScreen;
    private PendingRequestsScreen pendingRequestsScreen;
    private CompletedRequestsScreen completedRequestsScreen;
    private LandlordScreen landlordScreen;

    /**
     * Private default constructor
     */
    private AppInstance() {
        this.initializeApp();
    }

    /**
     * Initializes a new App instance if it doesn't already exist.
     * Returns the instance of AppInstance
     * @return reference to an AppInstance
     */
    public static synchronized AppInstance getAppInstance() {
        if (appInstance == null) {
            appInstance = new AppInstance();
        }
        return appInstance;
    }

    public void initializeApp() {
        // set firebase to be the primary database
        primaryDatabase = new FirebaseRepository(FirebaseAuth.getInstance());
        // initialize App's local data handlers
        appDataHandlers = new DataHandlers();
    }

    public boolean isUserAuthenticated() {
        return this.user != null;
    }

    public DataHandlers getAppDataHandler() {
        return this.appDataHandlers;
    }

    public FirebaseRepository getPrimaryDatabase() {
        return this.primaryDatabase;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public boolean userIsPropertyManager() {
        return (user != null && user.getRole() == UserRoles.PROPERTY_MANAGER);
    }

    /**
     * Sets app's admin inbox
     * @param propertyManagerInbox PropertyManagerInbox instance
     */
    public void setPropertyManagerInbox(PropertyManagerInbox propertyManagerInbox) throws NullPointerException {
        if (Preconditions.isNotNull(propertyManagerInbox)) {
            this.propertyManagerInbox = propertyManagerInbox;
        } else {
            Log.e("setPropertyManagerInbox", "App instance received a null object for PropertyManagerInbox");
            throw new NullPointerException("Unable to create property manager inbox");
        }
    }

    /**
     * @return Returns PropertyManagerInbox if current user is PropertyManager
     * @throws IllegalAccessException if user is not PropertyManager but tries to access PropertyManagerInbox
     */
    public PropertyManagerInbox getPropertyManagerInbox() throws IllegalAccessException {
        if (userIsPropertyManager()) {
            return this.propertyManagerInbox;
        } else {
            Log.e("getPropertyManagerInbox", "Either user is null or user role is not PropertyManager");
            throw new IllegalAccessException("User does not have access to PropertyManager inbox");
        }
    }

    public PropertiesListScreen getPropertiesListScreen() {
        return propertiesListScreen;
    }

    public void setPropertiesListScreen(PropertiesListScreen propertiesListScreen) {
        this.propertiesListScreen = propertiesListScreen;
    }

    public PendingRequestsScreen getPendingRequestsScreen() {
        return pendingRequestsScreen;
    }

    public void setPendingRequestsScreen(PendingRequestsScreen pendingRequestsScreen) {
        this.pendingRequestsScreen = pendingRequestsScreen;
    }

    public LandlordScreen getLandlordScreen() {
        return landlordScreen;
    }

    public void setLandlordScreen(LandlordScreen landlordScreen) {
        this.landlordScreen = landlordScreen;
    }

    public CompletedRequestsScreen getCompletedRequestsScreen() {
        return completedRequestsScreen;
    }

    public void setCompletedRequestsScreen(CompletedRequestsScreen completedRequestsScreen) {
        this.completedRequestsScreen = completedRequestsScreen;
    }

    /**
     * Handle user logout
     */
    public void logoutUser() {
        // remove user
        this.setUser(null);
        // sign user out
        FirebaseAuth.getInstance().signOut();
    }

    public void notifyPropertiesListChanged() {
        if (propertiesListScreen != null) {
            propertiesListScreen.notifyDataChanged();
        }
    }
}
