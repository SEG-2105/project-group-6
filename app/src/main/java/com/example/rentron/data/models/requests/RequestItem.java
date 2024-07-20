package com.example.rentron.data.models.requests;

import com.example.rentron.ui.screens.search.SearchPropertyItem;

import java.io.Serializable;

public class RequestItem implements Serializable {
    private SearchPropertyItem searchPropertyItem;
    private int quantity;

    public RequestItem(SearchPropertyItem searchPropertyItem, int quantity) {
        this.searchPropertyItem = searchPropertyItem;
        this.quantity = quantity;
    }

    public SearchPropertyItem getSearchPropertyItem() {
        return searchPropertyItem;
    }

    public void setSearchPropertyItem(SearchPropertyItem searchPropertyItem) {
        this.searchPropertyItem = searchPropertyItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {

        // Process: checking if comparing to self
        if (o == this) {
            // Output
            return true;
        }

        // Process: checking if comparing another request item
        if (!(o instanceof RequestItem)) { //not request item
            // Output
            return false;
        }

        // Variable Declaration
        RequestItem requestItem = (RequestItem) o;

        // Output: checking if ids are the same
        return requestItem.getSearchPropertyItem().getId().equals(this.getSearchPropertyItem().getId());
    }
}
