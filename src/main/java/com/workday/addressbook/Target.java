package com.workday.addressbook;

/**
 * Thing that can be associated with a name
 * 
 */
public abstract class Target {
    protected final String value;

    public Target(String value) {
        super();
        Preconditions.checkNotNull(value, "value cannot be null");
        Preconditions.checkArgument(value.isEmpty(), "value cannot be empty string");
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
