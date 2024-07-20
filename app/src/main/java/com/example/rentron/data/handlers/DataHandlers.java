package com.example.rentron.data.handlers;

public class DataHandlers {
    // instance handlers
    private final UserHandler userHandler;
    private final InboxHandler inboxHandler;
    private final PropertyHandler propertyHandler;
    private final RequestHandler requestHandler;

    // instantiate repository and handlers
    public DataHandlers() {
        this.userHandler = new UserHandler();
        this.inboxHandler = new InboxHandler();
        this.propertyHandler = new PropertyHandler();
        this.requestHandler = new RequestHandler();
    }

    public UserHandler getUserHandler() {
        return userHandler;
    }

    public InboxHandler getInboxHandler() { return inboxHandler; }

    public PropertyHandler getPropertyHandler() { return propertyHandler; }

    public RequestHandler getRequestHandler() { return requestHandler; }
}