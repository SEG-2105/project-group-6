package com.example.rentron.utils;

import java.util.List;

public abstract class Preconditions {

    /**
     * Checks if the provided object is not null.
     *
     * @param obj the object to check
     * @return true if the object is not null, else false
     */
    static public boolean isNotNull(Object obj) {
        return (obj != null);
    }

    /**
     * Checks if the provided String object is not an empty string or null.
     *
     * @param strObj the String object to check
     * @return true if the string is not null and not empty, else false
     */
    static public boolean isNotEmptyString(String strObj) {
        return isNotNull(strObj) && (strObj.length() != 0);
    }

    /**
     * Checks if the provided list object is not an empty list or null.
     *
     * @param list the list to check
     * @return true if the list is not null and not empty, else false
     */
    static public boolean isNotEmptyList(List list) {
        return isNotNull(list) && (!list.isEmpty());
    }
}
