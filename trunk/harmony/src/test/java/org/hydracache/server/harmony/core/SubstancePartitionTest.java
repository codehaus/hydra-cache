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
package org.hydracache.server.harmony.core;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.util.TestUtils;
import org.jgroups.stack.IpAddress;
import org.junit.Before;
import org.junit.Test;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author nzhu
 * 
 */
@SuppressWarnings("unchecked")
public class SubstancePartitionTest {

    private HashFunction hashFunction = new KetamaBasedHashFunction();

    private JGroupsNode nodeA;
    private JGroupsNode nodeB;
    private JGroupsNode nodeC;

    private List<Identity> serverIds;

    @Before
    public void setup() throws Exception {
        nodeA = new JGroupsNode(new Identity(TestUtils.getStableLocalAddress(),
                80), new IpAddress(800));
        nodeB = new JGroupsNode(new Identity(TestUtils.getStableLocalAddress(),
                81), new IpAddress(810));
        nodeC = new JGroupsNode(new Identity(TestUtils.getStableLocalAddress(),
                82), new IpAddress(820));

        serverIds = Arrays.asList(new Identity[] { nodeA.getId(),
                nodeB.getId(), nodeC.getId() });
    }

    @Test
    public void testNext() {
        SubstancePartition partition = new SubstancePartition(hashFunction,
                serverIds);

        assertEquals("Node A id should be returned", nodeA.getId(), partition
                .get(nodeA.getId().toString()));

        assertEquals("Node B id should be returned", nodeC.getId(), partition
                .next(nodeB.getId()));

        assertEquals("Node C id should be returned", nodeA.getId(), partition
                .next(nodeC.getId()));

        assertEquals("Node A id should be returned", nodeB.getId(), partition
                .next(nodeA.getId()));
    }
}
