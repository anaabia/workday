package com.workday.addressbook;

/**
 * Utility methods for validating method arguments
 * 
 */
public class Preconditions {

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkNotNull(Object reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
    }
}
