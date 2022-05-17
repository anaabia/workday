package com.workday.addressbook;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StandardBook
        implements BookExt {
    protected final Map<Name, Set<Target>> addresses;

    public StandardBook() {
        super();
        addresses = new HashMap<>();
    }

    @Override
    public boolean add(Name name, Target target) {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkNotNull(target, "target cannot be null");
        Preconditions.checkArgument(
                target instanceof Alias && !addresses.containsKey(target),
                "Name not already in book shouldn't be allowed as target");

        Preconditions.checkArgument(
                name instanceof Alias
                && addresses.containsKey(name)
                && addresses.get(name).size() == 1
                && !addresses.get(name).contains(target),
                "Alias mapped to more than one target shouldn't be allowed");

        Preconditions.checkArgument(name instanceof Group
                && target instanceof Group
                && addresses.containsKey(target)
                && addresses.get(target).contains(name),
                "Target mapped have already been existed in Name");

        Set<Target> targets = addresses.get(name);
        if (targets == null) {
            targets = new HashSet<>();
            addresses.put(name, targets);
        }
        return targets.add(target);
    }

    @Override
    public boolean delete(Name name, Target target) {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkNotNull(target, "target cannot be null");

        Preconditions.checkArgument(name instanceof Group
                && addresses.containsKey(name)
                && addresses.get(name).size() == 1
                && addresses.values().stream().anyMatch(address -> address.contains(name)),
                "Deleting member from singleton group that is target shouldn't be allowed");


        Set<Target> targets = addresses.get(name);
        if (targets != null && targets.contains(target)) {
            if (targets.size() > 1) {
                return targets.remove(target);
            }
            return addresses.remove(name) != null;
        }
        return false;
    }

    @Override
    public Set<Address> lookup(Name name) {
        Preconditions.checkNotNull(name, "name");

        Set<Address> results = new HashSet<Address>();
        Set<Target> targets = addresses.get(name);
        if (targets != null) {
            for (Target target : targets) {
                if (target instanceof Address) {
                    results.add((Address) target);
                }
                else {
                    findAddressLookingGroups(results, target);
                }
            }
        }
        return results;
    }

    private void findAddressLookingGroups(Set<Address> results, Target target) {
        Queue<Target> queue = new LinkedList<>(addresses.get(target).stream().collect(Collectors.toList()));
        while (!queue.isEmpty()) {
            Target currentTarget = queue.remove();
            if (currentTarget instanceof Address) {
                results.add((Address) currentTarget);
                continue;
            }
            Set<Target> addressesFounded = addresses.get(currentTarget).stream().filter(address -> address instanceof Address).collect(Collectors.toSet());
            if (!addressesFounded.isEmpty()) {
                results.addAll(addressesFounded.stream().map(address -> (Address) address).collect(Collectors.toSet()));
            } else {
                // add groups/Alias to find addresses
                queue.addAll(addresses.get(currentTarget).stream()
                        .filter(value -> !(value instanceof Address))
                        .collect(Collectors.toSet()));
            }
        }
    }

//    @Override
//    public Set<Address> lookup(Name name) {
//        Preconditions.checkNotNull(name, "name");
//
//        Set<Address> results = new HashSet<Address>();
//        Set<Target> targets = addresses.get(name);
//        if (targets != null) {
//            for (Target target : targets) {
//                if (target instanceof Address) {
//                    results.add((Address) target);
//                } else {
//                    // resolve alias or group
//                    results.addAll(lookup((Name) target));
//                }
//            }
//        }
//        return results;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEW_INDENT = " ";
        String NEW_LINE = System.getProperty("line.separator");
        sb.append("{Book ");
        for (Map.Entry<Name, Set<Target>> addr : addresses.entrySet()) {
            sb.append(NEW_LINE).append(NEW_INDENT).append(addr.getKey()).append(
                    " => [");
            boolean first = true;
            for (Target t : addr.getValue()) {
                if (first) {
                    first = false;
                }
                else {
                    sb.append(", ");
                }
                sb.append(t);
            }
            sb.append("]");
        }
        sb.append(NEW_LINE).append("}EndBook");
        return sb.toString();
    }

    public Iterator<Name> getNames(boolean sortAsc) {
        Comparator<Name> sorter = Comparator.comparing(Name::getValue);
        return addresses.keySet().stream()
                .sorted(sortAsc ? sorter.reversed() : sorter)
                .collect(Collectors.toList()).iterator();
    }

    public Set<Name> lookup(Address address) {
        Preconditions.checkNotNull(address, "address");
        Set<Name> results = lookUpAddressByAlias(address, addresses);

        Map<Name, Set<Target>> groups = addresses.entrySet().stream()
                .filter(entry -> !results.contains(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));


        for (Map.Entry<Name, Set<Target>> entry : groups.entrySet()) {
            results.addAll(findGroup(entry.getKey(), entry.getValue(), results));
        }

        return results;
    }

//    private boolean findGroup(Name key, Set<Target> list, Set<Name> namesFounded, Set<Name> namesByGroup) {
//        if (list.stream().anyMatch(value -> value instanceof Group)) {
//            for (Target group : list) {
//                if (findGroup((Name) group, addresses.get(group), namesFounded, namesByGroup)) {
//                    namesByGroup.add(key);
//                }
//            }
//        } else if (list.stream().anyMatch(value -> value instanceof Alias && namesFounded.contains(value))) {
//            return true;
//        }
//        return false;
//    }

    private Set<Name> findGroup(Name key, Set<Target> list, Set<Name> namesFounded) {
        Set<Name> namesByGroup = new HashSet<>();
        // iterate the class group
        if (list.stream().anyMatch(value -> value instanceof Group)) {
            findNameIteratorGroup(key, list, namesFounded, namesByGroup);
        // verify the commons class
        } else if (list.stream().anyMatch(value -> value instanceof Alias && namesFounded.contains(value))) {
            namesByGroup.add(key);
        }
        return namesByGroup;
    }

    private void findNameIteratorGroup(Name key, Set<Target> list, Set<Name> namesFounded, Set<Name> namesByGroup) {
        Queue<Target> queue = new LinkedList<>(list.stream().collect(Collectors.toList()));
        while (!queue.isEmpty()) {
            Target current = queue.remove();
            if (current instanceof Group) {
                // if find some class, which contains the address, inside the group
                if (anyMatch(value -> !(value instanceof Group) && namesFounded.contains(value), current)) {
                    namesByGroup.add(key);
                // if find some group class inside the group
                } else if (anyMatch(value -> value instanceof Group, current)) {
                    queue.addAll(new LinkedList<>(addresses.get(current).stream()
                            .collect(Collectors.toList())));
                }
            } else {
                if(current instanceof Alias && namesFounded.contains(current)){
                    namesByGroup.add(key);
                }
            }
        }
    }

    private boolean anyMatch(Predicate predicate, Target current) {
        return addresses.get(current).stream().anyMatch(predicate);
    }

    private Set<Name> lookUpAddressByAlias(Address address, Map<Name, Set<Target>> addressesSearch) {
        return addressesSearch.entrySet().stream()
                .filter(value -> value.getKey() instanceof Alias && value.getValue().contains(address))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
