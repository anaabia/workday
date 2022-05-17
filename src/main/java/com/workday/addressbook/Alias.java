package com.workday.addressbook;

import java.util.Arrays;

/**
 * Name that corresponds to an address or another name
 *
 */
public final class Alias
    extends Name {

    public Alias(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Alias)) {
            return false;
        }
        Alias otherAlias = (Alias) other;
        return value.equals(otherAlias.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { Alias.class, value });
    }

    @Override
    public String toString() {
        return "{Alias '" + value + "'}";
    }
}
