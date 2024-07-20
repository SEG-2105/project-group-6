package com.example.rentron.ui.screens.search;

import com.example.rentron.data.models.Landlord;
import com.example.rentron.data.models.properties.Property;
import com.example.rentron.data.models.requests.LandlordInfo;

import java.io.Serializable;
import java.util.UUID;

public class SearchPropertyItem implements Serializable {
    private UUID id;
    private Property property;
    private LandlordInfo landlord;

    public SearchPropertyItem(Property property, LandlordInfo landlord) {
        this.setId();
        this.setProperty(property);
        this.setLandlord(landlord);
    }

    public void setId() {
        this.id = UUID.randomUUID();
    }

    public String getId() {
        return this.id.toString();
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public LandlordInfo getLandlord() {
        return landlord;
    }

    public void setLandlord(Landlord landlord) {
        this.landlord = new LandlordInfo(landlord);
    }

    public void setLandlord(LandlordInfo landlord) {
        this.landlord = landlord;
    }
}
