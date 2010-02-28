/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hydracache.server.data.versioning;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.data.AbstractVersionTest;
import org.junit.Test;

/**
 * @author David Dossot (david@dossot.net)
 */
public class IncrementTest extends AbstractVersionTest {
    @Test
    public void create() {
        final Increment a1 = new Increment(A);
        assertEquals(1, a1.getValue());
        assertEquals(A, a1.getNodeId());

        assertTrue(a1.toString().contains("1"));

        final Increment a1bis = new Increment(A);
        assertEquals(1, a1bis.getValue());
        assertEquals(A, a1bis.getNodeId());

        assertEquals(a1, a1bis);
        assertEquals(a1.hashCode(), a1bis.hashCode());

        final Increment b1 = new Increment(B);
        assertEquals(1, b1.getValue());
        assertEquals(B, b1.getNodeId());

        assertThat(a1, not(b1));
    }

    @Test
    public void increment() {
        final Increment a2 = (Increment) new Increment(A).incrementFor(A);
        assertEquals(A, a2.getNodeId());
        assertEquals(2, a2.getValue());

        final Increment b2 = (Increment) new Increment(A).incrementFor(B);
        assertEquals(B, b2.getNodeId());
        assertEquals(2, b2.getValue());
    }

    @Test
    public void isNotDescendantOfVectorClock() {
        final Increment i = new Increment(A);
        final VectorClock vc = new VectorClock(A);
        assertFalse(i + "::" + vc, i.isDescendantOf(vc));
    }

    @Test
    public void isNotDescendantOfSame() {
        final Increment a1 = new Increment(A);
        assertFalse(a1.isDescendantOf(a1));

        final Increment a1bis = new Increment(A);
        assertFalse(a1bis.isDescendantOf(a1));
    }

    @Test
    public void isDescendantOfIncrementedSameNode() {
        final Increment a1 = new Increment(A);
        final Increment a2 = (Increment) a1.incrementFor(A);

        assertTrue(a2 + "::" + a1, a2.isDescendantOf(a1));
        assertFalse(a1 + "::" + a2, a1.isDescendantOf(a2));
    }

    @Test
    public void isDescendantOfIncrementedOtherNode() {
        final Increment a1 = new Increment(A);
        final Increment b2 = (Increment) a1.incrementFor(B);

        assertTrue(b2 + "::" + a1, b2.isDescendantOf(a1));
        assertFalse(a1 + "::" + b2, a1.isDescendantOf(b2));
    }
}
