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
package org.hydracache.client.http;

import java.util.Collections;
import static org.junit.Assert.*;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.hydracache.data.hashing.NativeHashFunction;
import org.hydracache.server.Identity;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.core.SubstancePartition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public class PartitionAwareHydraCacheClientTest {

    private PartitionAwareHydraCacheClient client;

    private Identity defaultIdentity;

    @Before
    public void beforeTests() throws Exception {
        this.defaultIdentity = new Identity(8080);

        List<Identity> ids = Collections.singletonList(defaultIdentity);
        SubstancePartition partition = new SubstancePartition(
                new NativeHashFunction(), ids);
        client = new PartitionAwareHydraCacheClient(partition);
    }

    @Test
    public void testShouldReadData() throws Exception {
        final String key = "key";
        HttpClient httpClient = new MockHttpClient();
        client.setHttpClient(httpClient);

        Data data = client.get(key);
        
        Assert.assertNull(data);
    }

    @Test
    public void ensureUrlConstructionCorrectness() {
        String key = "session893475";
        Identity localhostId = new Identity(8080);

        String url = client.constructUri(key, localhostId);

        assertEquals("URL construction is incorrect", "http://"
                + localhostId.getAddress().getHostName() + ":8080/" + key, url);
    }
}
