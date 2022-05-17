package com.workday.addressbook;

import java.util.Iterator;
import java.util.Set;

public interface BookExt
    extends Book {

    /**
     * Returns list of all names in the book (sorted alphabetically)
     * 
     * @param sortAsc true if sorted ascending, false descending
     * @return the sorted list of names
     */
    Iterator<Name> getNames(boolean sortAsc);

    /**
     * A reverse lookup that returns the set of names mapped (directly or
     * indirectly) to the specified address
     * 
     * @param address
     * @return the set of names mapped to the address
     */
    Set<Name> lookup(Address address);

}
