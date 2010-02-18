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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
    
    @Before
    public void setupBeforeTestMethod(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ensureMessageCanDeliver() throws Exception {
        int testPort = 8000;
        
        Identity targetNode = new Identity(testPort);
        RequestMessage message = new RequestMessage();

        Messager messenger = new Messager(transport);

        ResponseMessage expectedResponseMsg = new ResponseMessage(true);
        
        // stub
        when(transport.sendRequest(message)).thenReturn(expectedResponseMsg);

        SubstancePartition nodePartition = new SubstancePartition(
                new KetamaBasedHashFunction(), Arrays.asList(targetNode));

        messenger.sendMessage(targetNode, nodePartition, message);
        
        verify(transport).establishConnection(anyString(), eq(testPort));
        verify(transport).sendRequest(message);
        verify(transport).cleanUpConnection();
    }

}
