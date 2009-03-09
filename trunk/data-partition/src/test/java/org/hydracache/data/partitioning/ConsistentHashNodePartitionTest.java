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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.NativeHashFunction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test basic functionality of consistent hashing.
 * 
 * @author Tan Quach, Nick Zhu
 * @since 1.0
 */
public class ConsistentHashNodePartitionTest {

    private List<ServerNode> serverNodes;
    private HashFunction hashFunction;

    @Before
    public void setUp() {
        hashFunction = new NativeHashFunction();
        serverNodes = new LinkedList<ServerNode>();
    }

    @Test
    public void shouldReturnNullIfNoServerNodes() throws Exception {
        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes);
        ServerNode serverNode = circle.get("4");
        Assert.assertNull(serverNode);
    }

    @Test
    public void shouldReturnServerNode() {
        final ServerNode A = new ServerNode(1);
        serverNodes.add(A);
        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes);

        Assert.assertEquals(A, circle.get("1"));
    }

    @Test
    public void shouldAlwaysReturnSameNode() {
        final ServerNode A = new ServerNode(1);
        final ServerNode B = new ServerNode(10);
        final ServerNode C = new ServerNode(50);

        // Create a circle of 2 replicas
        serverNodes.add(A);
        serverNodes.add(B);
        serverNodes.add(C);

        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes);

        // First we verify that the node we get is closest to server node B.
        String data1 = "10";

        Assert.assertEquals(A, circle.get(data1)); // should get B
        Assert.assertEquals(A, circle.get(data1)); // and again
        Assert.assertEquals(A, circle.get(data1)); // ... and one more time.
    }

    @Test
    public void shouldSendToNearestNeighbourIfNodeIsRemoved() throws Exception {
        final ServerNode A = new ServerNode(1);
        final ServerNode B = new ServerNode(10);
        final ServerNode C = new ServerNode(50);

        // Create a circle of 2 replicas
        serverNodes.add(A);
        serverNodes.add(B);
        serverNodes.add(C);

        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes);

        // First we verify that the node we get is closest to server node B.
        String data1 = "10";

        Assert.assertEquals(A, circle.get(data1)); // should get B
        Assert.assertEquals(A, circle.get(data1)); // and again
        circle.remove(A);
        Assert.assertEquals(B, circle.get(data1)); // ... and now to the nearest
        // neighbour.
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
        // Create a circle of 3 closely placed nodes
        serverNodes.add(A);
        serverNodes.add(B);
        serverNodes.add(C);

        ConsistentHashNodePartition<ServerNode> circle = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes, 100000);
        
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
            String randomKey = RandomStringUtils.randomAlphanumeric(20);
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

        assertTrue("Distribution difference is greater than 15%",
                counterDifference < (sampleSize * 0.15));
    }

    private final class ServerNode {
        private int loc;

        public ServerNode(int loc) {
            this.loc = loc;
        }

        @Override
        public int hashCode() {
            return loc;
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
            if (loc != other.loc)
                return false;
            return true;
        }
    }
}
