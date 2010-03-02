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
package org.hydracache.client.partition;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.hydracache.client.HydraCacheAdminClient;
import org.hydracache.client.transport.NullTransport;
import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.Identity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Tan Quach
 * @since 1.0
 */
public class PartitionAwareClientTest {
    private HydraCacheAdminClient service;
    
    private NullTransport transport;

    @Mock
    private Transport mockTransport;

    @Before
    public void beforeTestMethods() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void afterTestMethods() throws Exception {
    }

    @Test
    public void shouldReturnEmptyListWhenCurrentPartitionIsNull()
            throws Exception {
        this.transport = new NullTransport();
        this.service = new PartitionAwareClient(Arrays
                .asList(new Identity(8080)), this.transport);

        ResponseMessage responseMessage = new ResponseMessage(true);
        responseMessage
                .setResponseBody("[{\"port\":8080,\"ip\":\"127.0.0.1\"}]"
                        .getBytes());
        this.transport.setResponseMessage(responseMessage);

        List<Identity> nodes = this.service.listNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        this.transport.setResponseMessage(null);
    }

    @Test
    public void ensureNodeIsRemovedIfFailToSendMessage() throws Exception {
        Identity node = new Identity(8080);

        when(mockTransport.sendRequest(any(RequestMessage.class))).thenThrow(
                new RuntimeException());

        PartitionAwareClient client = new PartitionAwareClient(Arrays
                .asList(node), mockTransport);

        NodePartition<Identity> partition = client.getNodePartition();

        assertTrue("Partition should contain the ID", partition.contains(node));

        try {
            client.put("key", "value");
            fail("Should have thrown exception");
        } catch (Exception e) {
            assertFalse("Partition should not contain the ID any more",
                    partition.contains(node));
        }
    }
}
