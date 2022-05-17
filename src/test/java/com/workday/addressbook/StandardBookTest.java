package com.workday.addressbook;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class StandardBookTest {

    // email addresses
    final Address jimAddr = new Address("jim.halpert@theoffice.com");
    final Address michaelAddr = new Address("michael.scott@theoffice.com");
    final Address dwightAddr = new Address("dwight.schrute@theoffice.com");

    // primary aliases
    final Alias jimAlias = new Alias("Jim");
    final Alias michaelAlias = new Alias("Michael");
    final Alias dwightAlias = new Alias("Dwight");

    // secondary aliases
    final Alias bigTunaAlias = new Alias("Big Tuna");

    // groups
    final Group mgrGroup = new Group("Manager");
    final Group fireMarshalGroup = new Group("Fire Marshal");

    final BookExt book = new StandardBook();
    private Group threeMarshal;

    @Test
    void addingNewMappingShouldModifyBook() {
        assertTrue(book.add(jimAlias, jimAddr));
    }

    @Test
    void addingDuplicateMappingShouldNotModifyBook() {
        book.add(jimAlias, jimAddr);

        assertFalse(book.add(jimAlias, jimAddr));
    }

    @Test
    void addingAliasMappedToMoreThanOneTargetShouldNotBeAllowed() {
        book.add(jimAlias, jimAddr);

        assertThrows(IllegalArgumentException.class, () -> {
            book.add(jimAlias, michaelAddr);
        });
    }

    @Test
    void nameNotAlreadyInBookShouldNotBeAllowedAsTarget() {
        assertThrows(IllegalArgumentException.class, () -> {
            book.add(dwightAlias, michaelAlias);
        });
    }

    @Test
    void deletingAliasShouldModifyBook() {
        book.add(jimAlias, jimAddr);

        assertTrue(book.delete(jimAlias, jimAddr));
    }

    @Test
    void deletingAliasThatNotExist() {
        book.add(jimAlias, jimAddr);

        assertFalse(book.delete(jimAlias, dwightAddr));
    }

    @Test
    void deletingGroupMemberShouldModifyBook() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);

        assertTrue(book.delete(mgrGroup, dwightAlias));
    }

    @Test
    void deletingMemberFromNonSingletonGroupThatIsTargetShouldBeAllowed() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);

        assertTrue(book.delete(mgrGroup, dwightAlias));
    }

    @Test
    void deletingMemberFromSingletonGroupThatIsTargetShouldNotBeAllowed() {
        book.add(michaelAlias, michaelAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(fireMarshalGroup, mgrGroup);

        assertThrows(IllegalArgumentException.class, () ->
            assertTrue(book.delete(mgrGroup, michaelAlias)), "Deleting member from singleton group that is target shouldn't be allowed");
    }

    @Test
    void deletingMemberFromSingletonGroupThatIsNotTargetShouldBeAllowed() {
        book.add(michaelAlias, michaelAddr);
        book.add(mgrGroup, michaelAlias);

        assertTrue(book.delete(mgrGroup, michaelAlias));
    }

    @Test
    void lookingUpAddressesInEmptyBook() {
        assertTrue(book.lookup(jimAlias).isEmpty());
    }

    @Test
    void lookingUpAddressFromAliasOneStepAway() {
        book.add(jimAlias, jimAddr);

        assertTrue(book.lookup(jimAlias).contains(jimAddr));
    }

    @Test
    void lookingUpAddressFromAliasTwoStepsAway() {
        book.add(jimAlias, jimAddr);
        book.add(bigTunaAlias, jimAlias);

        assertTrue(book.lookup(bigTunaAlias).contains(jimAddr));
    }

    @Test
    void lookupUpAddressesFromGroupTwoStepsAway() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);

        Set<Address> addrs = book.lookup(fireMarshalGroup);

        assertTrue(addrs.contains(michaelAddr));
        assertTrue(addrs.contains(dwightAddr));
    }

    @Test
    void lookupUpNameByAddressFromGroupTwoStepsAway() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);

        Set<Name> names = book.lookup(dwightAddr);

        assertTrue(names.contains(mgrGroup));
        assertTrue(names.contains(dwightAlias));
        assertTrue(names.contains(fireMarshalGroup));
    }

    @Test
    void lookingUpNameByAddressFromAliasOneStepAway() {
        book.add(jimAlias, jimAddr);

        assertTrue(book.lookup(jimAddr).contains(jimAlias));
    }

    @Test
    void lookingUpNameByAddressFromAliasTwoStepsAway() {
        book.add(jimAlias, jimAddr);
        book.add(bigTunaAlias, jimAlias);

        Set<Name> names = book.lookup(jimAddr);

        assertTrue(names.contains(jimAlias));
        assertTrue(names.contains(bigTunaAlias));
    }

    @Test
    void lookingUpNameByAddressFromAliasAndGroupStepsAway() {
        book.add(jimAlias, jimAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(mgrGroup, jimAlias);
        book.add(fireMarshalGroup, dwightAlias);
        book.add(mgrGroup, fireMarshalGroup);

        Set<Name> names = book.lookup(jimAddr);

        assertTrue(names.contains(jimAlias));
        assertTrue(names.contains(mgrGroup));
    }

    @Test
    void testStackOverflowErrorLookingUpByAddress () {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(jimAlias, jimAddr);
        book.add(mgrGroup, jimAlias);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, fireMarshalGroup);

        assertThrows(IllegalArgumentException.class, () ->
                assertTrue( book.add(fireMarshalGroup, mgrGroup)),
                        "Target mapped have already been existed in Name");

        book.add(fireMarshalGroup, dwightAlias);

        Set<Address> addrs = book.lookup(fireMarshalGroup);

        assertTrue(addrs.contains(dwightAddr));
    }

    @Test
    void lookupUpNameByAddressFromGroupThreeStepsAway() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(jimAlias, jimAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);
        threeMarshal = new Group("Three Marshal");
        book.add(threeMarshal, fireMarshalGroup);

        Set<Name> names = book.lookup(dwightAddr);

        assertTrue(names.contains(mgrGroup));
        assertTrue(names.contains(dwightAlias));
        assertTrue(names.contains(threeMarshal));
        assertTrue(names.contains(fireMarshalGroup));
    }

    @Test
    void lookupUpAddressByNameFromGroupThreeStepsAway() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(jimAlias, jimAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);
        threeMarshal = new Group("Three Marshal");
        book.add(threeMarshal, fireMarshalGroup);

        Set<Address> names = book.lookup(threeMarshal);

        assertTrue(names.contains(michaelAddr));
        assertTrue(names.contains(dwightAddr));
    }

    @Test
    void getNamesSortedAsc() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(jimAlias, jimAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);

        Iterator<Name> names = book.getNames(true);

        List<Name> actualList = new ArrayList<>();
        names.forEachRemaining(actualList::add);
        assertTrue(actualList.get(4).equals(dwightAlias));
        assertTrue(actualList.get(0).equals(michaelAlias));
    }

    @Test
    void getNamesSortedDesc() {
        book.add(michaelAlias, michaelAddr);
        book.add(dwightAlias, dwightAddr);
        book.add(jimAlias, jimAddr);
        book.add(mgrGroup, michaelAlias);
        book.add(mgrGroup, dwightAlias);
        book.add(fireMarshalGroup, mgrGroup);


        Iterator<Name> names = book.getNames(false);

        List<Name> actualList = new ArrayList<>();
        names.forEachRemaining(actualList::add);
        assertTrue(actualList.get(0).equals(dwightAlias));
        assertTrue(actualList.get(4).equals(michaelAlias));
    }
}
