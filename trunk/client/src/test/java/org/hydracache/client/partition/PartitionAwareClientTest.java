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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.hydracache.client.HydraCacheAdminClient;
import org.hydracache.client.transport.NullTransport;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.server.Identity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Tan Quach
 * @since 1.0
 */
public class PartitionAwareClientTest {
    private HydraCacheAdminClient service;
    private NullTransport transport;

    @Before
    public void beforeTestMethods() throws Exception {
        this.transport = new NullTransport();

        this.service = new PartitionAwareClient(Arrays.asList(new Identity(8080)), this.transport);
    }
    
    @After
    public void afterTestMethods() throws Exception {
        this.transport.setResponseMessage(null);
    }
    
    @Test
    public void shouldReturnEmptyListWhenCurrentPartitionIsNull() throws Exception {
        ResponseMessage responseMessage = new ResponseMessage(true);
        responseMessage.setResponseBody("[{\"port\":8080,\"ip\":\"127.0.0.1\"}]".getBytes());
        this.transport.setResponseMessage(responseMessage);
        
        List<Identity> nodes = this.service.listNodes();
        assertNotNull(nodes);
        assertFalse(nodes.isEmpty());
    }
}
