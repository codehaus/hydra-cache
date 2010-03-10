package org.hydracache.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleSet<T> implements Iterable<T> {

    protected Set<T> container = new LinkedHashSet<T>();

    public SimpleSet() {
    }

    public SimpleSet(Collection<T> collection) {
        container.addAll(collection);
    }

    public int size() {
        return container.size();
    }

    public boolean contains(T element) {
        return container.contains(element);
    }

    public void add(T element) {
        container.add(element);
    }

    @Override
    public Iterator<T> iterator() {
        return container.iterator();
    }

}