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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.data.AbstractVersionTest;
import org.junit.Test;

/**
 * @author David Dossot (david@dossot.net)
 */
public class VectorClockEntryTest extends AbstractVersionTest {

    @Test
    public void canBeStringRendered() {
        assertNotNull(new VectorClockEntry(A, 1).toString());
    }

    @Test
    public void sameIdentity() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry a1bis = new VectorClockEntry(A, 1);

        assertEquals(a1, a1bis);
        assertEquals(a1.hashCode(), a1bis.hashCode());
    }

    @Test
    public void differentIdentities() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry a2 = new VectorClockEntry(A, 2);
        final VectorClockEntry b1 = new VectorClockEntry(B, 1);

        assertThat(a1, is(not(a2)));
        assertThat(a1.hashCode(), is(not(a2.hashCode())));
        assertThat(a1, is(not(b1)));
        assertThat(a1.hashCode(), is(not(b1.hashCode())));
    }

    @Test
    public void timeStampCorrect() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry a1bis = new VectorClockEntry(A, 1);

        assertTrue(a1.getTimestamp() >= a1bis.getTimestamp());
    }

    @Test
    public void increment() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry a2 = new VectorClockEntry(A, 2);
        assertEquals(a2, a1.increment());
    }

    @Test
    public void sameDoesNotdescendFrom() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry a1bis = new VectorClockEntry(A, 1);

        assertFalse(a1.descendFrom(a1bis));
        assertFalse(a1bis.descendFrom(a1));
    }

    @Test
    public void differentDoesNotdescendFrom() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry b1 = new VectorClockEntry(B, 1);
        final VectorClockEntry c2 = new VectorClockEntry(C, 2);

        assertFalse(a1 + "::" + b1, a1.descendFrom(b1));
        assertFalse(b1 + "::" + a1, b1.descendFrom(a1));
        assertFalse(a1 + "::" + c2, a1.descendFrom(c2));
        assertFalse(c2 + "::" + a1, c2.descendFrom(a1));
    }

    @Test
    public void descendFrom() {
        final VectorClockEntry a1 = new VectorClockEntry(A, 1);
        final VectorClockEntry a2 = new VectorClockEntry(A, 2);
        final VectorClockEntry a3 = new VectorClockEntry(A, 3);

        assertTrue(a2 + "::" + a1, a2.descendFrom(a1));
        assertFalse(a1 + "::" + a2, a1.descendFrom(a2));

        assertTrue(a3 + "::" + a1, a3.descendFrom(a1));
        assertFalse(a1 + "::" + a3, a1.descendFrom(a2));

        assertTrue(a3 + "::" + a2, a3.descendFrom(a2));
        assertFalse(a2 + "::" + a3, a2.descendFrom(a3));
    }
}
