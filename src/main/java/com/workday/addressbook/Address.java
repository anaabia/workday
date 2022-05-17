package com.workday.addressbook;

import java.util.Arrays;

/**
 * Email address
 * 
 */
public final class Address
    extends Target {

    public Address(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Address)) {
            return false;
        }
        Address otherAddress = (Address) other;
        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { Address.class, value });
    }

    @Override
    public String toString() {
        return "{Address '" + value + "'}";
    }
}
