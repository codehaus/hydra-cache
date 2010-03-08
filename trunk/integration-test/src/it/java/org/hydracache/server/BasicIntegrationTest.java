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
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.partition.PartitionAwareClient;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author nzhu
 * 
 */
public class BasicIntegrationTest {
    private static final String INTEGRATION_TEST_STORAGE_CONTEXT = "integrationTest";

    private static Logger log = Logger.getLogger(BasicIntegrationTest.class);

    private static final int PORT_NUMBER = 7345;

    private static final String SERVER_NAME = "localhost";

    private IncrementVersionFactory versionFactory = new IncrementVersionFactory();

    private Identity serverId;

    private PartitionAwareClient client;

    @Before
    public void setup() throws Exception {
        versionFactory.setIdentityMarshaller(new IdentityMarshaller());

        serverId = new Identity(InetAddress.getByName(SERVER_NAME), PORT_NUMBER);

        client = new PartitionAwareClient(Arrays.asList(serverId));
    }

    @Test
    public void testBasicOperations() throws Exception {
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        String testKey = createTestKey();

        assertPutAndGet(testKey);

        assertDeletion(testKey);

        stopwatch.stop();

        log.info("Took [" + stopwatch.getTime()
                + "]ms to perform a put and get pair + one deletion");
    }

    private void assertDeletion(String testKey) throws Exception {
        boolean deletionResult = client.delete(
                INTEGRATION_TEST_STORAGE_CONTEXT, testKey);

        assertTrue("Deletion should be successful", deletionResult);

        assertEquals(testKey + " should be deleted", null, client.get(
                INTEGRATION_TEST_STORAGE_CONTEXT, testKey));
    }

    @Test
    public void testRepetitivePutAndGet() throws Exception {
        StopWatch stopwatch = new StopWatch();

        stopwatch.start();

        int numberOfTests = 100;

        String testKey = createTestKey();

        for (int i = 0; i < numberOfTests; i++) {
            assertPutAndGet(testKey);
        }

        assertDeletion(testKey);

        stopwatch.stop();

        log.info("Took [" + stopwatch.getTime() + "]ms to perform "
                + numberOfTests + " put and get pairs + one deletion");
    }

    private void assertPutAndGet(String testKey) throws Exception {
        client.get(INTEGRATION_TEST_STORAGE_CONTEXT, testKey);

        String data = RandomStringUtils.randomAlphanumeric(RandomUtils
                .nextInt(500));

        client.put(INTEGRATION_TEST_STORAGE_CONTEXT, testKey, data);

        Object retrievedData = client.get(INTEGRATION_TEST_STORAGE_CONTEXT,
                testKey);

        assertEquals("Retrieved data is incorrect", data, retrievedData);
    }

    private String createTestKey() {
        return "TestKey";
    }

}
