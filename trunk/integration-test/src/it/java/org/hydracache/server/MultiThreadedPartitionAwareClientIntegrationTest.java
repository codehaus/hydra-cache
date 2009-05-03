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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.partition.PartitionAwareClient;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.junit.Test;

/**
 * Note: having too many threads and a small key pool can cause a live-lock in
 * this test. 
 */
public class MultiThreadedPartitionAwareClientIntegrationTest extends
        AbstractHydraClientIntegrationTest {

    private static Logger log = Logger
            .getLogger(MultiThreadedPartitionAwareClientIntegrationTest.class);

    private static final String SERVER_NAME = "localhost";

    private boolean failed = false;

    @Test
    public void testRepetitivePutAndGet() throws Exception {
        int numberOfThreads = 5;

        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            createTesterThread(doneLatch).start();
        }

        try {
            doneLatch.await();
        } catch (InterruptedException e) {
        }

        if (failed)
            fail("Failure detected");
    }

    private Thread createTesterThread(final CountDownLatch doneLatch)
            throws Exception {
        return new TesterThread(doneLatch);
    }

    private final class TesterThread extends Thread {
        private final CountDownLatch doneLatch;
        private IncrementVersionFactory versionFactory = new IncrementVersionFactory();
        private PartitionAwareClient client;

        private TesterThread(CountDownLatch doneLatch) throws Exception {
            this.doneLatch = doneLatch;

            versionFactory.setIdentityMarshaller(new IdentityMarshaller());
            client = new PartitionAwareClient(Arrays.asList(new Identity(
                    InetAddress.getByName(SERVER_NAME), 8080),
                    new Identity(InetAddress.getByName(SERVER_NAME),
                            8081), new Identity(InetAddress
                            .getByName(SERVER_NAME), 8082),
                    new Identity(InetAddress.getByName(SERVER_NAME),
                            8083)));
        }

        @Override
        public void run() {
            try {
                StopWatch stopwatch = new StopWatch();
                stopwatch.start();

                int numberOfRepetition = 30;

                for (int i = 0; i < numberOfRepetition; i++) {
                    doPutAndGet();
                }

                stopwatch.stop();

                log.info("Took [" + stopwatch.getTime() + "]ms to perform ["
                        + numberOfRepetition + "] put and get pair");
            } catch (Exception e) {
                e.printStackTrace();
                failed = true;
            } finally {
                doneLatch.countDown();
            }
        }

        private void doPutAndGet() throws Exception {
            String key = getKeyFromThePool();

            String data = createRandomDataSample(key);

            log.info("Putting key: " + key);

            tryPut(key, data, 1);
        }

        private void tryPut(String key, String data, int retryCount)
                throws Exception {
            try {
                client.put(key, data);
                log.info("Put was successful at [" + retryCount + "] try");
            } catch (VersionConflictException e) {
                refresh(key);
                tryPut(key, data, ++retryCount);
            }
        }

        private void refresh(String key) throws Exception {
            log.info("Version conflict detected, refreshing [" + key + "]");
            Object newData = client.get(key);
            assertNotNull("Data retrieve after conflict is null", newData);
        }
    }

}
