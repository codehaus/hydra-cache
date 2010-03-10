/*
 * Copyright 2010 the original author or authors.
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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.SubstancePartition;
import org.hydracache.server.Identity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MessagerTest {
    @Mock
    private Transport transport;

    private int testPort = 8000;
    private Identity targetNode = new Identity(testPort);
    private RequestMessage message = new RequestMessage();
    private Messager messenger;

    @Before
    public void setupBeforeTestMethod() {
        MockitoAnnotations.initMocks(this);

        messenger = new Messager(transport);
    }

    @Test
    public void ensureMessageCanDeliver() throws Exception {
        ResponseMessage expectedResponseMsg = new ResponseMessage(true);

        stubSuccessfulSend(expectedResponseMsg);

        SubstancePartition nodePartition = new SubstancePartition(
                new KetamaBasedHashFunction(), Arrays.asList(targetNode));

        messenger.sendMessage(targetNode, nodePartition, message);

        verify(transport).establishConnection(anyString(), eq(testPort));
        verify(transport).sendRequest(message);
        verify(transport).cleanUpConnection();
    }

    private void stubSuccessfulSend(ResponseMessage expectedResponseMsg)
            throws Exception {
        when(transport.sendRequest(message)).thenReturn(expectedResponseMsg);
    }

    @Test
    public void ensureNodeIsRemovedAfterExceptionIsCaught() throws Exception {
        stubFailedSend();

        SubstancePartition nodePartition = new SubstancePartition(
                new KetamaBasedHashFunction(), Arrays.asList(targetNode));

        try {
            messenger.sendMessage(targetNode, nodePartition, message);
            fail("Should have failed");
        } catch (Exception ex) {
            assertFalse("Target node should have removed from the partition",
                    nodePartition.contains(targetNode));

            verify(transport).establishConnection(anyString(), eq(testPort));
            verify(transport).sendRequest(message);
            verify(transport).cleanUpConnection();
        }
    }

    private void stubFailedSend() throws Exception {
        when(transport.sendRequest(message)).thenThrow(new RuntimeException());
    }

    @Test
    public void ensureSecondNodeIsRetriedAfterFailure() throws Exception {
        Identity secondNode = new Identity(8008);
        ResponseMessage expectedResponseMsg = new ResponseMessage(true);

        stubUnsuccessfulConnect();
        stubSuccessfulSend(expectedResponseMsg);

        SubstancePartition nodePartition = new SubstancePartition(
                new KetamaBasedHashFunction(), Arrays.asList(targetNode,
                        secondNode));

        ResponseMessage responseMsg = messenger.sendMessage(targetNode,
                nodePartition, message);

        assertEquals("Response message is incorrect", expectedResponseMsg, responseMsg);

        assertFalse("Target node should have removed from the partition",
                nodePartition.contains(targetNode));

        verify(transport).establishConnection(anyString(), eq(testPort));
        verify(transport).establishConnection(anyString(), eq(8008));
        verify(transport).sendRequest(message);
        verify(transport, times(2)).cleanUpConnection();
    }

    private void stubUnsuccessfulConnect() {
        when(transport.establishConnection(anyString(), eq(testPort)))
                .thenThrow(new RuntimeException());
    }

}
