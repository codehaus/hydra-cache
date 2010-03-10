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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.junit.Before;
import org.junit.Test;

/**
 * Test basic multi-thread concurrency handling of consistent hashing.
 * 
 * @author Nick Zhu
 */
public class ConsistentHashNodePartitionTestConcurrency {

    private List<ServerNode> serverNodes;
    private HashFunction hashFunction;

    private boolean failed = false;

    @Before
    public void setUp() {
        hashFunction = new KetamaBasedHashFunction();
        serverNodes = new LinkedList<ServerNode>();
    }

    @Test(timeout = 10000)
    public void ensureThreadSafty() throws InterruptedException {
        for (int i = 0; i < 100; i++)
            serverNodes.add(new ServerNode(i));

        final ConsistentHashNodePartition<ServerNode> hashRing = new ConsistentHashNodePartition<ServerNode>(
                hashFunction, serverNodes, 200);

        int N = 1000;
        final CountDownLatch doneLatch = new CountDownLatch(N);

        for (int i = 0; i < N; i++) {
            startTestThread(hashRing, doneLatch);
        }

        waitForAllThreadToComplete(doneLatch);

        if (failed)
            fail("Failure detected");
    }

    private void startTestThread(
            final ConsistentHashNodePartition<ServerNode> circle,
            final CountDownLatch doneLatch) {
        new Thread() {
            @Override
            public void run() {
                try {
                    messWithHashRing(circle);

                    doneLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                    failed = true;
                }
            }

            private void messWithHashRing(
                    final ConsistentHashNodePartition<ServerNode> circle) {
                ServerNode node = new ServerNode(RandomUtils.nextInt(100));
                circle.remove(node);

                Thread.yield();

                ServerNode n = circle.get(RandomStringUtils.randomNumeric(5));
                assertNotNull(n);

                Thread.yield();

                circle.add(node);
            }

        }.start();
    }

    private void waitForAllThreadToComplete(final CountDownLatch doneLatch) {
        try {
            doneLatch.await();
        } catch (InterruptedException e) {
        }
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
