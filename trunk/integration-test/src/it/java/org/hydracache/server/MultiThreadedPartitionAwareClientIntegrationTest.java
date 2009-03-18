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
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.http.PartitionAwareHydraCacheClient;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class MultiThreadedPartitionAwareClientIntegrationTest {

    private static Logger log = Logger
            .getLogger(MultiThreadedPartitionAwareClientIntegrationTest.class);

    private static final String SERVER_NAME = "localhost";

    private boolean failed = false;

    @Test
    public void testRepetitivePutAndGet() throws Exception {
        int N = 25;
        final CountDownLatch doneLatch = new CountDownLatch(N);

        for (int i = 0; i < N; i++) {
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
        private Map<String, Data> localDataStorage = new HashMap<String, Data>();
        private IncrementVersionFactory versionFactory = new IncrementVersionFactory();
        private PartitionAwareHydraCacheClient client;
        private NodePartition<Identity> partition;

        private TesterThread(CountDownLatch doneLatch) throws Exception {
            this.doneLatch = doneLatch;

            versionFactory.setIdentityMarshaller(new IdentityMarshaller());

            partition = new ConsistentHashNodePartition<Identity>(
                    new KetamaBasedHashFunction(), Arrays.asList(new Identity(
                            InetAddress.getByName(SERVER_NAME), 8080),
                            new Identity(InetAddress.getByName(SERVER_NAME),
                                    8081), new Identity(InetAddress
                                    .getByName(SERVER_NAME), 8082),
                            new Identity(InetAddress.getByName(SERVER_NAME),
                                    8083)));

            client = new PartitionAwareHydraCacheClient(partition);
        }

        @Override
        public void run() {
            try {
                StopWatch stopwatch = new StopWatch();
                stopwatch.start();
                int numberOfRepetition = 30;
                for (int i = 0; i < numberOfRepetition; i++) {
                    assertPutAndGet();
                }
                stopwatch.stop();
                log.info("Took [" + stopwatch.getTime() + "]ms to perform ["
                        + numberOfRepetition + "] put and get pair");
                assertDataIntegrity();
            } catch (Exception e) {
                e.printStackTrace();
                failed = true;
            } finally {
                doneLatch.countDown();
            }
        }

        private void assertPutAndGet() throws Exception {
            String randomKey = createRandomKey();

            Data data = createRandomDataSample(randomKey);
            localDataStorage.put(randomKey, data);
            client.put(randomKey, data);
            
            Thread.sleep(100);

            Object retrievedData = client.get(randomKey);

            assertEquals("Retrieved data is incorrect", data, retrievedData);
        }

        private String createRandomKey() {
            String randomKey = UUID.randomUUID().toString();
            return randomKey;
        }

        private Data createRandomDataSample(String randomKey) {
            Data data = new Data();

            data.setKeyHash((long) randomKey.hashCode());
            data.setVersion(versionFactory.create(partition.get(randomKey)));
            data.setContent(RandomStringUtils.randomAlphanumeric(200)
                    .getBytes());

            return data;
        }

        private void assertDataIntegrity() throws Exception {
            for (String key : localDataStorage.keySet()) {
                Data localData = localDataStorage.get(key);
                Object remoteData = client.get(key);
                if (!localData.equals(remoteData))
                    failed = true;
            }
        }
    }

}
