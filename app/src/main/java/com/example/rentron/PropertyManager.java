package com.example.rentron;

import java.util.ArrayList;
import java.util.List;

public class PropertyManager {
    private static List<Property> properties = new ArrayList<>();

    public static void addProperty(Property property) {
        properties.add(property);
    }

    public static List<Property> getAllProperties() {
        return properties;
    }
}
