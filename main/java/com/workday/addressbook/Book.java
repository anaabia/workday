package com.workday.addressbook;

import java.util.Set;

/**
 * Maintains collection of mappings of names to email addresses
 * 
 */
public interface Book {

    /**
     * Adds specified mapping to book. A mapping associates a name with a target.
     * If target is a name, that name must already belong to the book.
     * 
     * @param name the alias or group to associate with target
     * @param target the address, alias, or group that is target of name
     * @return true if mapping does not exist and was added
     */
    boolean add(Name name, Target target);

    /**
     * Deletes specified mapping from book
     * 
     * @param name the alias or group associated with target
     * @param target the address, alias, or group that is target of name
     * @return true if mapping exists and was deleted
     */
    boolean delete(Name name, Target target);

    /**
     * Finds the set of addresses associated with the specified name, resolving
     * any name chains along the way
     * 
     * @param name the start name
     * @return the set of addresses associated (directly or indirectly) with name
     */
    Set<Address> lookup(Name name);

}