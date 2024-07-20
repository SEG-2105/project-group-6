package com.example.rentron.data.models;

import com.example.rentron.data.entity_models.UserEntityModel;
import com.example.rentron.data.models.requests.RequestItem;
import com.example.rentron.ui.screens.search.SearchProperties;
import com.example.rentron.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * This class instantiates an instance of Client for Rentron App
 * Child Class of User
 */
public class Client extends User {

    /**
     * Stores request items in cart
     */
    Map<RequestItem, Boolean> cart;
    public final Requests REQUESTS;

    private CreditCard clientCreditCard;

    private SearchProperties searchProperties;
    /**
     * Create a Client object
     * @param firstName First name of the client
     * @param lastName Last name of the client
     * @param email email of the client
     * @param password password for the client
     * @param address address of the client
     * @param role Role of the client
     * @param clientCreditCard credit card info of the client
     */
    public Client(String firstName, String lastName, String email, String password, Address address, UserRoles role, CreditCard clientCreditCard)  throws IllegalArgumentException {
        // instantiate Client's data members
        super(firstName, lastName, email, password, address, role);
        // userId should have been created for the client by this point
        this.setClientCreditCard(clientCreditCard);
        this.cart = new HashMap<>(); //empty cart
        this.REQUESTS = new Requests();
        // instantiate a SearchProperties instance to store searchable properties
        this.searchProperties = new SearchProperties();
    }

    /**
     * Create a Client object
     * @param clientData a UserEntityModel object containing unvalidated user details
     * @param clientAddress an Address object containing validated address info
     * @param clientCreditCard credit card info of the client
     */
    public Client(UserEntityModel clientData, Address clientAddress, CreditCard clientCreditCard) throws IllegalArgumentException {
        // instantiate Client's data members
        super(clientData, clientAddress);
        this.setClientCreditCard(clientCreditCard);

        this.cart = new HashMap<>(); //empty cart
        this.REQUESTS = new Requests();
        // instantiate a SearchProperties instance to store searchable properties
        this.searchProperties = new SearchProperties();
    }

    @Override
    public void setUserId(String userId) {
        super.setUserId(userId);
        // if we have a valid credit card, update client ID there as well
        if (clientCreditCard != null) {
            clientCreditCard.setClientId(userId);
        }
    }

    /**
     * Get the client's credit card
     * @return CreditCard object
     */
    public CreditCard getClientCreditCard() {
        return clientCreditCard;
    }

    /**
     * Method to add credit card for a client
     * @param clientCreditCard a CreditCard object
     */
    public void setClientCreditCard(CreditCard clientCreditCard) {
        this.clientCreditCard = clientCreditCard;
        // update user id of new credit card as well, if user id exists (i.e., user registered on database)
        if (this.getUserId() != null && !this.getUserId().equals("")) {
            this.clientCreditCard.setClientId(this.getUserId());
        }
    }

    /**
     * Update request items in cart
     * @param requestItem instance of RequestItem
     */
    public void updateRequestItem(RequestItem requestItem) {
        if (requestItem.getQuantity() == 0) {
            removeFromCart(requestItem); //calling helper method to remove item
        } else {
            this.cart.put(requestItem, true);
        }
    }

    /**
     * this helper method overrides the remove method for the cart map
     * and uses the custom equals method for comparing request items
     * @param requestItem the item to be removed from cart
     */
    private void removeFromCart(RequestItem requestItem) {

        // Process: looping through the cart
        for (RequestItem oi : this.cart.keySet()) {

            // Process: find the key
            if (oi.equals(requestItem)) { //equals

                this.cart.remove(oi); //removing from cart

                break; //stop loop

            }

        }

    }

    /**
     * this method completely clears the cart
     */
    public void clearCart() {
        this.cart.clear(); //cart cleared

    }

    public Map<RequestItem, Boolean> getCart() {
        return cart;
    }

    /**
     * method to get request item information of a property present in client's cart
     * @return if property present returns an instance of that Property, else null
     */
    public RequestItem getRequestItem(String propertyId) {
        // cart should not be null or empty
        if (Preconditions.isNotNull(this.cart) && !this.cart.isEmpty()) {
            for (RequestItem requestItem : this.cart.keySet()) {
                // if current request item has the property we're looking for
                if (requestItem.getSearchPropertyItem().getProperty().getPropertyID().equals(propertyId)) {
                    // return the request item
                    return requestItem;
                }
            }
        }
        // if cart is empty or no request item found for the property
        return null;
    }

    public SearchProperties getSearchProperties() {
        return searchProperties;
    }
}
