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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.http.HttpHydraCacheClient;
import org.hydracache.data.hashing.NativeHashFunction;
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
public class PartitionAwarePutAndGetTest {
    private static Logger log = Logger
            .getLogger(PartitionAwarePutAndGetTest.class);

    private static final String SERVER_NAME = "localhost";

    private IncrementVersionFactory versionFactory = new IncrementVersionFactory();

    private HttpHydraCacheClient client;

    private NodePartition<Identity> partition;

    private Map<String, Data> localDataStorage;

    @Before
    public void setup() throws Exception {
        versionFactory.setIdentityMarshaller(new IdentityMarshaller());

        partition = new ConsistentHashNodePartition<Identity>(
                new NativeHashFunction(), Arrays.asList(new Identity(
                        InetAddress.getByName(SERVER_NAME), 8080),
                        new Identity(InetAddress.getByName(SERVER_NAME), 8081),
                        new Identity(InetAddress.getByName(SERVER_NAME), 8082),
                        new Identity(InetAddress.getByName(SERVER_NAME), 8083)));

        client = new HttpHydraCacheClient(partition);

        localDataStorage = new HashMap<String, Data>();
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

    private void assertPutAndGet() {
        String randomKey = createRandomKey();

        Data data = createRandomDataSample(randomKey);
        localDataStorage.put(randomKey, data);
        client.put(randomKey, data);

        Data retrievedData = client.get(randomKey);

        assertEquals("Retrieved data is incorrect", data, retrievedData);
    }

    private String createRandomKey() {
        String randomKey = RandomStringUtils.randomAlphanumeric(10);
        return randomKey;
    }

    private Data createRandomDataSample(String randomKey) {
        Data data = new Data();

        data.setKeyHash((long) randomKey.hashCode());
        data.setVersion(versionFactory.create(partition.get(randomKey)));
        data.setContent(RandomStringUtils.randomAlphanumeric(200).getBytes());

        return data;
    }

    private void assertDataIntegrity() {
        for (String key : localDataStorage.keySet()) {
            Data localData = localDataStorage.get(key);
            Data remoteData = client.get(key);

            assertEquals(
                    "Remote and local copy of the same data are not identical",
                    localData, remoteData);
        }
    }

}
