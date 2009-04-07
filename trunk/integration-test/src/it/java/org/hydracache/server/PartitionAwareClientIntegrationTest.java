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
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.http.PartitionAwareClient;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class PartitionAwareClientIntegrationTest extends
        AbstractHydraClientIntegrationTest {
    private static Logger log = Logger
            .getLogger(PartitionAwareClientIntegrationTest.class);

    private static final String SERVER_NAME = "localhost";

    private IncrementVersionFactory versionFactory = new IncrementVersionFactory();

    private PartitionAwareClient client;

    private NodePartition<Identity> partition;

    private Map<String, String> localDataStorage;

    @Before
    public void setup() throws Exception {
        versionFactory.setIdentityMarshaller(new IdentityMarshaller());

        partition = new ConsistentHashNodePartition<Identity>(
                new KetamaBasedHashFunction(), Arrays.asList(new Identity(
                        InetAddress.getByName(SERVER_NAME), 8080),
                        new Identity(InetAddress.getByName(SERVER_NAME), 8081),
                        new Identity(InetAddress.getByName(SERVER_NAME), 8082),
                        new Identity(InetAddress.getByName(SERVER_NAME), 8083)));

        client = new PartitionAwareClient(partition);

        localDataStorage = new HashMap<String, String>();
    }

    @Test
    public void testUpdates() throws Exception {
        String randomKey = createRandomKey();

        String data = RandomStringUtils.randomAlphanumeric(200);
        client.put(randomKey, data);

        for (int i = 0; i < 10; i++) {
            data = RandomStringUtils.randomAlphanumeric(RandomUtils
                    .nextInt(500));
            client.put(randomKey, data);
        }

        assertEquals("Updated data is incorrect", data, client.get(randomKey));
    }

    @Test
    public void ensureNotFoundDataReturnsAsNull() throws IOException {
        String uniqueKey = UUID.randomUUID().toString();

        Object result = client.get(uniqueKey);

        assertNull("Result should be null", result);
    }

    @Test
    public void testBasicPutAndGet() throws Exception {
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        assertPutAndGet();

        stopwatch.stop();

        assertDataIntegrity();

        log.info("Took [" + stopwatch.getTime()
                + "]ms to perform a put and get pair");
    }

    @Test
    public void testRepetitivePutAndGet() throws Exception {
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        int numberOfRepetition = 100;

        for (int i = 0; i < numberOfRepetition; i++) {
            assertPutAndGet();
        }

        stopwatch.stop();

        assertDataIntegrity();

        log.info("Took [" + stopwatch.getTime() + "]ms to perform ["
                + numberOfRepetition + "] put and get pair");
    }

    private void assertPutAndGet() throws Exception {
        String randomKey = createRandomKey();

        String data = createRandomDataSample(randomKey);
        localDataStorage.put(randomKey, data);

        log.info("Putting key: " + randomKey);

        client.put(randomKey, data);

        Object retrievedData = client.get(randomKey);

        assertEquals("Retrieved data is incorrect", data, retrievedData);
    }

    private void assertDataIntegrity() throws Exception {
        for (String key : localDataStorage.keySet()) {
            String localData = localDataStorage.get(key);
            Object remoteData = client.get(key);

            assertEquals(
                    "Remote and local copy of the same data are not identical",
                    localData, remoteData);
        }
    }

}
