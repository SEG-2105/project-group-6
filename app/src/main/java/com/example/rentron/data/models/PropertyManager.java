package com.example.rentron.data.models;

/**
 * This class instantiates a single instance of Property Manager for Rentron App
 * Design Pattern: Singleton pattern -> https://www.geeksforgeeks.org/singleton-design-pattern/
 */
public class PropertyManager extends User {
    private static volatile PropertyManager rentronPropertyManager = null;

    /**
     * Create a single instance of property manager
     * @param firstName First name of the property manager
     * @param lastName Last name of the property manager
     * @param email email of the property manager
     * @param password password for the property manager
     * @param address address of the property manager
     * @param role Role of the property manager
     */
    private PropertyManager(String firstName, String lastName, String email, String password, Address address, UserRoles role) {
        // instantiate Property Manager's data members
        super(firstName, lastName, email, password, address, role);
    }

    /**
     * Constructor to instantiate a new Property Manager
     * @param userId userid of the property manager
     * @param firstName First name of the property manager
     * @param lastName Last name of the property manager
     * @param email email of the property manager
     */
    public PropertyManager(String userId, String firstName, String lastName, String email) {
        super(userId, firstName, lastName, email, UserRoles.PROPERTY_MANAGER);
    }

    public static PropertyManager getInstance(String firstName, String lastName, String email, String password, Address address, UserRoles role) {
        if(rentronPropertyManager == null) {

            //To make thread safe
            synchronized (PropertyManager.class) {
                // check again as multiple threads
                // can reach above step
                if(rentronPropertyManager == null)
                    rentronPropertyManager = new PropertyManager(firstName, lastName, email, password, address, role);
            }
        }
        return rentronPropertyManager;
    }
}
