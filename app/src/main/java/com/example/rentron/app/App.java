package com.example.rentron.app;

import com.example.rentron.data.handlers.InboxHandler;
import com.example.rentron.data.handlers.PropertyHandler;
import com.example.rentron.data.handlers.RequestHandler;
import com.example.rentron.data.handlers.UserHandler;
import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.Client;
import com.example.rentron.data.models.User;
import com.example.rentron.data.models.UserRoles;
import com.example.rentron.data.models.inbox.PropertyManagerInbox;
import com.example.rentron.data.sources.FirebaseRepository;

public class App {
    static AppInstance app = AppInstance.getAppInstance();
    static final public UserHandler USER_HANDLER = app.getAppDataHandler().getUserHandler();
    static final public PropertyHandler PROPERTY_HANDLER = app.getAppDataHandler().getPropertyHandler();
    static final public RequestHandler REQUEST_HANDLER = app.getAppDataHandler().getRequestHandler();

    public static AppInstance getAppInstance() {
        return app;
    }

    public static FirebaseRepository getPrimaryDatabase() {
        return app.getPrimaryDatabase();
    }

    public static User getUser() { return app.getUser(); }
    public static String getUserId() { return app.getUser().getUserId(); }

    public static UserHandler getUserHandler() {
        return app.getAppDataHandler().getUserHandler();
    }

    public static InboxHandler getInboxHandler() {
        return app.getAppDataHandler().getInboxHandler();
    }

    /**
     * Checks if there is a valid user and the user is Property Manager
     * @return true if current user is property manager, else false
     */
    public static boolean userIsPropertyManager() {
        return app.userIsPropertyManager();
    }

    /**
     * @return Returns PropertyManagerInbox if current user is property manager
     * @throws IllegalAccessException if user is not property manager but tries to access PropertyManagerInbox
     */
    public static PropertyManagerInbox getPropertyManagerInbox() throws IllegalAccessException {
        return app.getPropertyManagerInbox();
    }

    /**
     * Sets app's property manager inbox
     * @param PropertyManagerInbox PropertyManagerInbox instance
     */
    public static void setPropertyManagerInbox(PropertyManagerInbox PropertyManagerInbox) throws NullPointerException {
        app.setPropertyManagerInbox(PropertyManagerInbox);
    }

    /**
     * Helper method to get an instance of a logged in Client
     * @return instance of client, if authentication or user validation fails, returns null
     */
    public static Client getClient() {
        // if user is logged in and is a Client
        if (app.isUserAuthenticated() && app.getUser().getRole() == UserRoles.CLIENT) {
            try {
                return (Client) app.getUser();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Helper method to get an instance of a logged in Landlord
     * @return instance of landlord, if authentication or user validation fails, returns null
     */
    public static Landlord getLandlord() {
        // if user is logged in and is a Landlord
        if (app.isUserAuthenticated() && app.getUser().getRole() == UserRoles.LANDLORD) {
            try {
                return (Landlord) app.getUser();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
