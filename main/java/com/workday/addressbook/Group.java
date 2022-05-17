package com.workday.addressbook;

import java.util.Arrays;

/**
 * Name that corresponds to a collection of names or addresses
 *
 */
public final class Group
    extends Name {

    public Group(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Group)) {
            return false;
        }
        Group otherGroup = (Group) other;
        return value.equals(otherGroup.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] { Group.class, value });
    }

    @Override
    public String toString() {
        return "{Group '" + value + "'}";
    }
}
