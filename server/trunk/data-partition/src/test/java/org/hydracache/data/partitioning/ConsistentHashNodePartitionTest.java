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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partition.ConsistentHashable;
import org.hydracache.data.partition.ConsistentHashableString;
import org.hydracache.server.Identity;
import org.junit.Assert;

import static org.junit.Assert.*;

import org.junit.Before;
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
        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, Arrays.asList(A, B, C));

        Collection<ServerNode> servers = circle.getServers();

        assertEquals("Number of server is incorrect", 3, servers.size());
        assertTrue("Server list should contain all servers", servers.containsAll(Arrays.asList(A, B, C)));
    }

    @Test
    public void ensureEmptyPartitionReturnNullFromGetByHash() {
        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, new LinkedList<ServerNode>());

        ServerNode serverNode = circle.getByHash(hashFunction.hash(new ConsistentHashableString("4")));

        Assert.assertNull(serverNode);
    }

    @Test
    public void shouldReturnNullIfNoServerNodes() throws Exception {
        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, new LinkedList<ServerNode>());

        ServerNode serverNode = circle.get("4");

        Assert.assertNull(serverNode);
    }

    @Test
    public void shouldReturnServerNode() {
        final ServerNode A = new ServerNode(1);

        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, Arrays.asList(A));

        Assert.assertEquals(A, circle.get("1"));
    }

    @Test
    public void testContainsMethod() {
        ConsistentHashNodePartition<ServerNode> circle = givenStandardTestCircle();

        assertTrue("Circle should contain the node", circle.contains(A));
        assertTrue("Circle should contain the node", circle.contains(B));
        assertTrue("Circle should contain the node", circle.contains(C));
        assertFalse("Circle should not contain the node", circle
                .contains(new ServerNode(20)));
    }

    @Test
    public void shouldAlwaysReturnSameNode() {
        ConsistentHashNodePartition<ServerNode> circle = givenStandardTestCircle();

        // First we verify that the node we get is closest to server node B.
        String data1 = "10";

        Assert.assertEquals(C, circle.get(data1)); // should get B
        Assert.assertEquals(C, circle.get(data1)); // and again
        Assert.assertEquals(C, circle.get(data1)); // ... and one more time.
    }

    @Test
    public void shouldSendToNearestNeighbourIfNodeIsRemoved() throws Exception {
        ConsistentHashNodePartition<ServerNode> circle = givenStandardTestCircle();

        // First we verify that the node we get is closest to server node B.
        String data1 = "10";

        Assert.assertEquals(C, circle.get(data1)); // should get B
        Assert.assertEquals(C, circle.get(data1)); // and again
        circle.remove(C);
        Assert.assertEquals(A, circle.get(data1)); // ... and now to the nearest
        // neighbour.
    }

    private ConsistentHashNodePartition<ServerNode> givenStandardTestCircle() {
        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, Arrays.asList(A, B, C));
        return circle;
    }

    @Test
    public void ensureUniformDistribution() {
        int sampleSize = 10000;

        final ServerNode A = new ServerNode(1);
        final ServerNode B = new ServerNode(2);
        final ServerNode C = new ServerNode(3);

        ConsistentHashNodePartition<ServerNode> circle = givenCloselyPlacedNodesWithLargeReplicas(
                A, B, C);

        HashMap<ServerNode, Integer> counterMap = countSelectionOfEachNodeInSampleSpace(
                sampleSize, A, B, C, circle);

        assertUniformedDistribution(sampleSize, A, B, C, counterMap);
    }

    private ConsistentHashNodePartition<ServerNode> givenCloselyPlacedNodesWithLargeReplicas(
            final ServerNode A, final ServerNode B, final ServerNode C) {
        List<ServerNode> serverNodes = Arrays.asList(A, B, C);

        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes, 40);

        return circle;
    }

    private HashMap<ServerNode, Integer> countSelectionOfEachNodeInSampleSpace(
            int sampleSize, final ServerNode A, final ServerNode B,
            final ServerNode C, ConsistentHashNodePartition<ServerNode> circle) {
        HashMap<ServerNode, Integer> counterMap = new HashMap<ServerNode, Integer>();
        counterMap.put(A, 0);
        counterMap.put(B, 0);
        counterMap.put(C, 0);

        for (int i = 0; i < sampleSize; i++) {
            String randomKey = "" + RandomUtils.nextLong();
            ServerNode node = circle.get(randomKey);
            int counter = counterMap.get(node);
            counter++;
            counterMap.put(node, counter);
        }

        return counterMap;
    }

    private void assertUniformedDistribution(int sampleSize,
                                             final ServerNode A, final ServerNode B, final ServerNode C,
                                             HashMap<ServerNode, Integer> counterMap) {
        int maxCounter = NumberUtils.max(counterMap.get(A), counterMap.get(B),
                counterMap.get(C));
        int minCounter = NumberUtils.min(counterMap.get(A), counterMap.get(B),
                counterMap.get(C));

        int counterDifference = maxCounter - minCounter;

        System.out.println("maxCounter: " + maxCounter);
        System.out.println("minCounter: " + minCounter);
        System.out.println("Delta Counter: " + counterDifference);

        assertTrue("Distribution difference is greater than 20%",
                counterDifference < (sampleSize * 0.20));
    }

    private final class ServerNode implements ConsistentHashable {
        private int id;

        public ServerNode(int loc) {
            this.id = loc;
        }

        @Override
        public int hashCode() {
            return id;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.hydracache.data.partition.ConsistentHashable#consistentHash()
         */

        @Override
        public String getConsistentValue() {
            return "" + id;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ServerNode other = (ServerNode) obj;
            if (id != other.id)
                return false;
            return true;
        }
    }
}
