package org.hydracache.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.hydracache.util.SimpleSet;
import org.junit.Test;

public class SimpleSetTest {
    private Object obj1 = new Object();
    private Object obj2 = new Object();

    @Test
    public void ensureSimpleSetCanContainSubstancesThroughAdd() {
        SimpleSet<Object> set = new SimpleSet<Object>();

        set.add(obj1);
        set.add(obj2);

        assertEquals("Simple set size is incorrect", 2, set.size());
        assertTrue("Simple set should contain element", set.contains(obj2));
    }

    @Test
    public void ensureSimpleSetCanContainSubstancesThroughConstructor() {
        SimpleSet<Object> set = new SimpleSet<Object>(Arrays.asList(obj1, obj2));

        assertEquals("Simple set size is incorrect", 2, set.size());
        assertTrue("Simple set should contain element", set.contains(obj1));
    }

    @Test
    public void ensureSimpleSetDoesNotAllowDups() {
        SimpleSet<Object> set = new SimpleSet<Object>(Arrays.asList(obj1, obj1,
                obj2));

        assertEquals("Simple set size is incorrect", 2, set.size());
    }

    @Test
    public void ensureSimpleSetIsIterable() {
        SimpleSet<Object> set = new SimpleSet<Object>(Arrays.asList(obj1, obj2));

        for (Object obj : set) {
            assertTrue("Simple set should contain this instance", set
                    .contains(obj));
        }
    }

}
