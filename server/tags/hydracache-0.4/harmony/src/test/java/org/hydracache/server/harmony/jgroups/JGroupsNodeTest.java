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
package org.hydracache.server.harmony.jgroups;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class JGroupsNodeTest {
    private Node nodeA = new JGroupsNode(new Identity(8080), new IpAddress());
    private Node nodeA2 = new JGroupsNode(new Identity(8080), new IpAddress());
    private Node nodeB = new JGroupsNode(new Identity(8081), new IpAddress());

    @Test
    public void ensureNodeIdIsUsedAsIdentifier() throws Exception {
        assertEquals("Node A and A2 should be equal", nodeA, nodeA2);

        assertFalse("Node A and B should not be equal", nodeA.equals(nodeB));

        assertTrue("Node with same id should be equal", nodeA
                .equals(new JGroupsNode(nodeA.getId(), new IpAddress(7000))));
        
        assertEquals("ToString should be id based", nodeA.getId().toString(), nodeA.toString());
    }

    @Test
    public void ensureCompareNullAlwaysReturnInequal() {
        assertFalse("Node A and B should not be equal",
                nodeA.compareTo(null) == 0);
    }

    @Test
    public void ensureOnlyNodeIdIsUsedForCompare() {
        assertTrue("Node A and A2 should be equal",
                nodeA.compareTo(nodeA2) == 0);

        assertFalse("Node A and B should not be equal",
                nodeA.compareTo(nodeB) == 0);

        assertTrue("Node A and A2 should be equal",
                nodeA.compareTo(new JGroupsNode(nodeA.getId(), new IpAddress(
                        7000))) == 0);
    }

}
