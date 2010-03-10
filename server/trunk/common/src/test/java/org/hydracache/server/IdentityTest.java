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
package org.hydracache.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.Inet4Address;

import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class IdentityTest {

    private Identity idA = new Identity(80);
    private Identity idA2 = new Identity(80);
    private Identity idB = new Identity(81);

    @Test
    public void ensureCorrectIdentityEquals() throws Exception {
        Inet4Address address = (Inet4Address) Inet4Address.getLocalHost();

        short port = 80;

        Identity id1 = new Identity(address, port);

        assertEquals("Ids should be equal", id1, new Identity(address, port));

        assertFalse("Ids should not be equal", id1.equals(new Identity(address,
                (short) (port + 1))));
    }

    @Test
    public void ensureCorrectIdentityComparison() {
        assertTrue("Ids should be equal", idA.compareTo(idA) == 0);
        assertTrue("Ids should be equal", idA.compareTo(idA2) == 0);

        assertFalse("Ids should not be equal", idA.compareTo(idB) == 0);
    }

    @Test
    public void ensureNullCanBeCompared() {
        assertFalse("Ids should not be equal", idA.compareTo(null) == 0);
    }

}
