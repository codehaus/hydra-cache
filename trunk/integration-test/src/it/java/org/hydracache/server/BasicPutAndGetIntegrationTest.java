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

import java.net.InetAddress;
import java.util.Arrays;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.http.PartitionAwareHydraCacheClient;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class BasicPutAndGetIntegrationTest {
    private static Logger log = Logger.getLogger(BasicPutAndGetIntegrationTest.class);

    private static final int PORT_NUMBER = 8080;

    private static final String SERVER_NAME = "localhost";

    private IncrementVersionFactory versionFactory = new IncrementVersionFactory();

    private Identity serverId;

    private PartitionAwareHydraCacheClient client;

    @Before
    public void setup() throws Exception {
        versionFactory.setIdentityMarshaller(new IdentityMarshaller());

        serverId = new Identity(InetAddress.getByName(SERVER_NAME), PORT_NUMBER);

        NodePartition<Identity> partition = new ConsistentHashNodePartition<Identity>(
                new KetamaBasedHashFunction(), Arrays.asList(serverId));

        client = new PartitionAwareHydraCacheClient(partition);
    }

    @Test
    public void testBasicPutAndGet() throws Exception {
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        assertPutAndGet();

        stopwatch.stop();

        log.info("Took [" + stopwatch.getTime()
                + "]ms to perform a put and get pair");
    }

    @Test
    public void testRepetitivePutAndGet() throws Exception {
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        int numberOfTests = 100;

        for (int i = 0; i < numberOfTests; i++) {
            assertPutAndGet();
        }

        stopwatch.stop();

        log.info("Took [" + stopwatch.getTime() + "]ms to perform "
                + numberOfTests + " put and get pairs");
    }

    private void assertPutAndGet() throws Exception {
        String randomKey = createRandomKey();

        Data data = createRandomDataSample(randomKey);

        client.put(randomKey, data);

        Object retrievedData = client.get(randomKey);

        assertEquals("Retrieved data is incorrect", data, retrievedData);
    }

    private Data createRandomDataSample(String randomKey) {
        Data data = new Data();

        data.setKeyHash((long) randomKey.hashCode());
        data.setVersion(versionFactory.create(serverId));
        data.setContent(RandomStringUtils.randomAlphanumeric(200).getBytes());

        return data;
    }

    private String createRandomKey() {
        String randomKey = RandomStringUtils.randomAlphanumeric(10);
        return randomKey;
    }

}
