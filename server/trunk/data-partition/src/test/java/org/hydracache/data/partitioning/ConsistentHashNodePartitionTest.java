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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partition.ConsistentHashable;
import org.hydracache.data.partition.ConsistentHashableString;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test basic functionality of consistent hashing.
 * 
 * @author Tan Quach
 * @author Nick Zhu
 * @since 1.0
 */
public class ConsistentHashNodePartitionTest {

    private HashFunction hashFunction;
    private final ServerNode A = new ServerNode(1);
    private final ServerNode B = new ServerNode(10);
    private final ServerNode C = new ServerNode(50);

    @Before
    public void setUp() {
        hashFunction = new KetamaBasedHashFunction();
    }

    @Test
    public void ensureServerListRetrieval() {
        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                Arrays.asList(A, B, C));

        final Collection<ServerNode> servers = circle.getNodes();

        assertEquals("Number of server is incorrect", 3, servers.size());
        assertTrue("Server list should contain all servers", servers.containsAll(Arrays.asList(A, B, C)));
    }

    @Test
    public void ensureEmptyPartitionReturnNullFromGetByHash() {
        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                new LinkedList<ServerNode>());

        final ServerNode serverNode = circle.getByHash(hashFunction.hash(new ConsistentHashableString("4")));

        assertNull(serverNode);
    }

    @Test
    public void ensurePartitionIgnoresRemoveCallWithNull() {
        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                new LinkedList<ServerNode>());

        circle.remove(null);
        assertTrue(circle.getNodes().isEmpty());
    }

    @Test
    public void shouldReturnNullIfNoServerNodes() throws Exception {
        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                new LinkedList<ServerNode>());

        final ServerNode serverNode = circle.get("4");

        assertNull(serverNode);
    }

    @Test
    public void shouldReturnServerNode() {
        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                Arrays.asList(A));

        assertEquals(A, circle.get(A.getConsistentValue()));
    }

    @Test
    public void testContainsMethod() {
        final ConsistentHashNodePartition<ServerNode> circle = givenStandardTestCircle();

        assertTrue("Circle should contain the node", circle.contains(A));
        assertTrue("Circle should contain the node", circle.contains(B));
        assertTrue("Circle should contain the node", circle.contains(C));
        assertFalse("Circle should not contain the node", circle.contains(new ServerNode(20)));
    }

    @Test
    public void shouldAlwaysReturnSameNode() {
        final ConsistentHashNodePartition<ServerNode> circle = givenStandardTestCircle();

        // First we verify that the node we get is closest to server node B.
        final String data1 = "10";

        assertEquals(C, circle.get(data1)); // should get B
        assertEquals(C, circle.get(data1)); // and again
        assertEquals(C, circle.get(data1)); // ... and one more time.
    }

    @Test
    public void shouldSendToNearestNeighbourIfNodeIsRemoved() throws Exception {
        final ConsistentHashNodePartition<ServerNode> circle = givenStandardTestCircle();

        // First we verify that the node we get is closest to server node C
        final String data1 = "10";

        assertEquals(C, circle.get(data1)); // should get C
        assertEquals(C, circle.get(data1)); // and again
        circle.remove(C);

        // ... and now to the nearest neighbour.
        assertEquals(A, circle.get(data1));
    }

    @Test
    @Ignore
    /**
     * This test doesn't pass, but I'm not sure if it should or not.
     */
    public void shouldRetrieveSameNodeAfterAddingNewNode() throws Exception {
        final ConsistentHashNodePartition<ServerNode> partition = givenStandardTestCircle();

        // First we verify that the node we get is closest to server node.
        final String data1 = "2";

        assertEquals(C, partition.get(data1));

        // Add new node
        partition.add(new ServerNode(9000));

        // Should get same node
        assertEquals(A, partition.get(data1));
    }

    @Test
    public void ensureUniformDistribution() {
        final int sampleSize = 10000;

        final ServerNode A = new ServerNode(1);
        final ServerNode B = new ServerNode(2);
        final ServerNode C = new ServerNode(3);

        final ConsistentHashNodePartition<ServerNode> circle = givenCloselyPlacedNodesWithLargeReplicas(A, B, C);

        final HashMap<ServerNode, Integer> counterMap = countSelectionOfEachNodeInSampleSpace(sampleSize, A, B, C, circle);

        assertUniformedDistribution(sampleSize, A, B, C, counterMap);
    }

    private ConsistentHashNodePartition<ServerNode> givenStandardTestCircle() {
        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                Arrays.asList(A, B, C));
        return circle;
    }

    private ConsistentHashNodePartition<ServerNode> givenCloselyPlacedNodesWithLargeReplicas(final ServerNode A,
            final ServerNode B, final ServerNode C) {
        final List<ServerNode> serverNodes = Arrays.asList(A, B, C);

        final ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(hashFunction,
                serverNodes, 40);

        return circle;
    }

    private HashMap<ServerNode, Integer> countSelectionOfEachNodeInSampleSpace(final int sampleSize, final ServerNode A,
            final ServerNode B, final ServerNode C, final ConsistentHashNodePartition<ServerNode> circle) {
        final HashMap<ServerNode, Integer> counterMap = new HashMap<ServerNode, Integer>();
        counterMap.put(A, 0);
        counterMap.put(B, 0);
        counterMap.put(C, 0);

        for (int i = 0; i < sampleSize; i++) {
            final String randomKey = "" + RandomUtils.nextLong();
            final ServerNode node = circle.get(randomKey);
            int counter = counterMap.get(node);
            counter++;
            counterMap.put(node, counter);
        }

        return counterMap;
    }

    private void assertUniformedDistribution(final int sampleSize, final ServerNode A, final ServerNode B, final ServerNode C,
            final HashMap<ServerNode, Integer> counterMap) {
        final int maxCounter = NumberUtils.max(counterMap.get(A), counterMap.get(B), counterMap.get(C));
        final int minCounter = NumberUtils.min(counterMap.get(A), counterMap.get(B), counterMap.get(C));

        final int counterDifference = maxCounter - minCounter;

        assertTrue("Distribution difference is greater than 20%", counterDifference < (sampleSize * 0.20));
    }

    private final class ServerNode implements ConsistentHashable {
        private final int id;

        public ServerNode(final int loc) {
            id = loc;
        }

        @Override
        public int hashCode() {
            return id;
        }

        /*
         * (non-Javadoc)
         * @see org.hydracache.data.partition.ConsistentHashable#consistentHash()
         */

        @Override
        public String getConsistentValue() {
            return String.valueOf(id);
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ServerNode other = (ServerNode) obj;
            if (id != other.id) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ServerNode [id=" + id + "]";
        }
    }
}
