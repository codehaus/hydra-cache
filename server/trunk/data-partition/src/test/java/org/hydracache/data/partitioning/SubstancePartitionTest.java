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
package org.hydracache.data.partitioning;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.server.Identity;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class SubstancePartitionTest {

    private HashFunction hashFunction = new KetamaBasedHashFunction();

    private Identity nodeA;
    private Identity nodeB;
    private Identity nodeC;

    private List<Identity> serverIds;

    @Before
    public void setup() throws Exception {
        nodeA = new Identity(800);
        nodeB = new Identity(810);
        nodeC = new Identity(820);

        serverIds = Arrays.asList(nodeA, nodeB, nodeC);
    }

    @Test
    public void testNext() {
        SubstancePartition partition = new SubstancePartition(hashFunction,
                serverIds);

        assertEquals("Node A id should be returned", nodeA, partition
                .get(nodeA.toString()));

        assertEquals("Node C id should be returned", nodeC, partition
                .next(nodeA));

        assertEquals("Node A id should be returned", nodeC, partition
                .next(nodeA));

        assertEquals("Node B id should be returned", nodeA, partition
                .next(nodeB));
    }
}
