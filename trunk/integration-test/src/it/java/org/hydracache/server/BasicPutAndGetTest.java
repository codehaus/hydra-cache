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

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hydracache.client.http.HttpHydraCacheClient;
import org.hydracache.data.hashing.NativeHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class BasicPutAndGetTest {
    private static Logger log = Logger.getLogger(BasicPutAndGetTest.class);

    private static final String GREETING_MESSAGE = "Hellow Hydra! with some extra words so we have a sizable message "
            + "to test out the performance a bit. \n";

    private static final int PORT_NUMBER = 8080;

    private static final String SERVER_NAME = "localhost";

    private IncrementVersionFactory versionFactory = new IncrementVersionFactory();

    @Before
    public void setup() {
        versionFactory.setIdentityMarshaller(new IdentityMarshaller());
    }

    @Test
    public void testBasicClient() throws Exception {
        Identity localServerIdentity = new Identity(InetAddress
                .getByName(SERVER_NAME), PORT_NUMBER);

        NodePartition<Identity> partition = new ConsistentHashNodePartition<Identity>(
                new NativeHashFunction(), Arrays.asList(localServerIdentity));

        HttpHydraCacheClient client = new HttpHydraCacheClient(partition);

        Version version = versionFactory.create(localServerIdentity);

        String key = "greetingMessageKey";

        Data data = new Data();

        data.setKeyHash((long) key.hashCode());
        data.setVersion(version);
        data.setContent(GREETING_MESSAGE.getBytes());

        StopWatch stopwatch = new StopWatch();

        stopwatch.start();
        
        client.put(key, data);

        Data retrievedData = client.get(key);

        stopwatch.stop();

        log.info("Took [" + stopwatch.getTime()
                + "]ms to perform a put and get pair");

        assertEquals("Retrieved data is incorrect", data, retrievedData);
    }

}
