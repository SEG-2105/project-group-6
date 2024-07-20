package com.example.rentron.data.models.requests;

import com.example.rentron.data.models.properties.Property;

import java.io.Serializable;

public class PropertyInfo implements Serializable {

    String address;
    double price;
    int quantity;

    /**
     * Create an instance of PropertyInfo using what is stored in a request object in Firebase
     * @param address Address of the property
     * @param quantity Quantity of the property
     * @param price Current price of the property
     */
    public PropertyInfo(String address, double price, int quantity) {
        this.setAddress(address);
        this.setQuantity(quantity);
        this.setPrice(price);
    }

    /**
     * Create an instance of PropertyInfo using Property
     * @param property  property data used to create
     */
    // must set quantity afterwards
    public PropertyInfo (Property property) {
        this.setAddress(property.getAddress());
        this.setPrice(property.getPrice());
    }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

}
