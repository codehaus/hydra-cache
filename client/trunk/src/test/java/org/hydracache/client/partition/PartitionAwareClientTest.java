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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hydracache.client.transport.NullTransport;
import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.data.partitioning.SubstancePartition;
import org.hydracache.server.Identity;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * @author Tan Quach
 * @since 1.0
 */
public class PartitionAwareClientTest {
    private PartitionAwareClient client;

    private NullTransport nullTransport = new NullTransport();

    private Transport mockTransport;

    private Messenger messenger;

    private PartitionUpdatesPoller poller;

    @Before
    public void beforeTestMethods() throws Exception {
        mockTransport = mock(Transport.class);
        messenger = mock(Messenger.class);
        poller = mock(PartitionUpdatesPoller.class);
    }

    @After
    public void afterTestMethods() throws Exception {
        mockTransport = null;
        messenger = null;
        poller = null;
    }

    @Test
    public void testSuccessfulDeletionWithoutContext() throws Exception {
        client = new PartitionAwareClient(Arrays.asList(new Identity(8080)),
                mockTransport, poller);

        mockSuccessfulMessaging();

        client.setMessager(messenger);

        String context = null;
        String key = "testKey";

        boolean result = client.delete(key);

        assertTrue("Should be successful", result);

        ArgumentCaptor<RequestMessage> reqMsgCaptor = verifySendMessageAndCaptureRequest();

        assertRequestMsg("delete", context, key, reqMsgCaptor);
    }

    @Test
    public void testSuccessfulDeletionWithContext() throws Exception {
        client = new PartitionAwareClient(Arrays.asList(new Identity(8080)),
                mockTransport, poller);

        mockSuccessfulMessaging();

        client.setMessager(messenger);

        String context = "testContext";
        String key = "testKey";

        boolean result = client.delete(context, key);

        assertTrue("Should be successful", result);

        ArgumentCaptor<RequestMessage> reqMsgCaptor = verifySendMessageAndCaptureRequest();

        assertRequestMsg("delete", context, key, reqMsgCaptor);
    }

    private void mockSuccessfulMessaging() throws Exception {
        when(
                messenger.sendMessage(any(Identity.class),
                        any(SubstancePartition.class),
                        any(RequestMessage.class))).thenReturn(
                new ResponseMessage(true));
    }

    private ArgumentCaptor<RequestMessage> verifySendMessageAndCaptureRequest()
            throws Exception {
        ArgumentCaptor<RequestMessage> reqMsgCaptor = ArgumentCaptor
                .forClass(RequestMessage.class);
        verify(messenger, atLeastOnce()).sendMessage(any(Identity.class),
                any(SubstancePartition.class), reqMsgCaptor.capture());
        return reqMsgCaptor;
    }

    private void assertRequestMsg(String method, String context, String key,
            ArgumentCaptor<RequestMessage> reqMsgCaptor) {
        assertEquals("Method is incorrect", method, reqMsgCaptor.getValue()
                .getMethod());
        assertEquals("Context is incorrect", context, reqMsgCaptor.getValue()
                .getContext());
        assertEquals("Path is incorrect", key, reqMsgCaptor.getValue()
                .getPath());
    }
    
    @Test(expected=EmptySpaceException.class)
    public void getShouldThrowExceptionIfPartitionIsEmpty()
            throws Exception {
        client = new PartitionAwareClient(new ArrayList<Identity>(),
                nullTransport, poller);

        client.get("testKey");
    }
    
    @Test(expected=EmptySpaceException.class)
    public void deleteShouldThrowExceptionIfPartitionIsEmpty()
            throws Exception {
        client = new PartitionAwareClient(new ArrayList<Identity>(),
                nullTransport, poller);

        client.delete("testKey");
    }
    
    @Test(expected=EmptySpaceException.class)
    public void putShouldThrowExceptionIfPartitionIsEmpty()
            throws Exception {
        client = new PartitionAwareClient(new ArrayList<Identity>(),
                nullTransport, poller);

        client.put("testKey", "");
    }

    @Test
    public void shouldReturnEmptyListWhenCurrentPartitionIsNull()
            throws Exception {
        client = new PartitionAwareClient(Arrays.asList(new Identity(8080)),
                nullTransport, poller);

        ResponseMessage responseMessage = new ResponseMessage(true);
        responseMessage
                .setResponseBody("[{\"port\":8080,\"ip\":\"127.0.0.1\"}]"
                        .getBytes());
        nullTransport.setResponseMessage(responseMessage);

        List<Identity> nodes = this.client.listNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());

        nullTransport.setResponseMessage(null);
    }

    @Test
    public void ensureNodeIsRemovedIfFailToSendMessage() throws Exception {
        Identity node = new Identity(8080);

        when(mockTransport.sendRequest(any(RequestMessage.class))).thenThrow(
                new RuntimeException());

        PartitionAwareClient client = new PartitionAwareClient(
                Arrays.asList(node), mockTransport, poller);

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

    @Test
    public void ensureShutdownReleasesResources() throws Exception {
        client = new PartitionAwareClient(Arrays.asList(new Identity(8080)),
                nullTransport, poller);

        assertTrue("Client should be running", client.isRunning());

        client.shutdown();

        assertFalse("Client should be stopped", client.isRunning());
    }

    @Test
    public void ensureStorageInfoCanBeRetrieved() throws Exception {
        client = new PartitionAwareClient(Arrays.asList(new Identity(8080)),
                mockTransport, poller);

        ResponseMessage responseMsg = new ResponseMessage(true);
        JSONObject output = new JSONObject();
        output.put("testField", "100");
        StringWriter writer = new StringWriter();
        output.write(writer);
        responseMsg.setResponseBody(writer.toString().getBytes());

        when(
                messenger.sendMessage(any(Identity.class),
                        any(SubstancePartition.class),
                        any(RequestMessage.class))).thenReturn(responseMsg);

        client.setMessager(messenger);

        Map<String, String> info = client.getStorageInfo();

        assertEquals("Test field value is incorrect", "100",
                info.get("testField"));
    }

    @Test
    public void ensureNodeIsRemovedIfGetQueryFails() throws Exception {
        Identity node = new Identity(8080);

        when(mockTransport.sendRequest(any(RequestMessage.class))).thenThrow(
                new RuntimeException());

        PartitionAwareClient client = new PartitionAwareClient(
                Arrays.asList(node), mockTransport, poller);

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
