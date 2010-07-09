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
package org.hydracache.client.transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.hydracache.client.InternalHydraException;
import org.junit.Test;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class HttpTransportTest {
    
    @Test(expected=InternalHydraException.class)
    public void ensureInternalErrorThrowsException() throws Exception {
        HttpTransport transport = new HttpTransport();
        
        transport.establishConnection("localhost", 90);
        
        HttpClient mockHttpClient = mock(HttpClient.class);
        
        when(mockHttpClient.executeMethod(any(HttpMethod.class))).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        
        transport.httpClient = mockHttpClient; 
        
        RequestMessage requestMessage = new RequestMessage();
        
        requestMessage.setMethod("delete");
        requestMessage.setPath("testPath");
        
        transport.sendRequest(requestMessage);
    }

    @Test
    public void testDeleteRequestPathGeneration() {
        HttpTransport transport = new HttpTransport();

        transport.establishConnection("localhost", 90);

        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMethod("delete");
        requestMessage.setPath("testPath");

        HttpMethod method = transport.createHttpMethod(requestMessage);

        assertTrue("Method type is incorret", method instanceof DeleteMethod);
        assertEquals("Path is incorrect", "/testPath", method.getPath());
    }

    @Test
    public void testDeleteRequestPathGenerationWithContext() {
        HttpTransport transport = new HttpTransport();

        transport.establishConnection("localhost", 90);

        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMethod("delete");
        requestMessage.setPath("testPath");
        requestMessage.setContext("context");

        HttpMethod method = transport.createHttpMethod(requestMessage);

        assertTrue("Method type is incorret", method instanceof DeleteMethod);
        assertEquals("Path is incorrect", "/context/testPath", method.getPath());
    }

    @Test
    public void testGetRequestPathGeneration() {
        HttpTransport transport = new HttpTransport();

        transport.establishConnection("localhost", 90);

        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMethod("get");
        requestMessage.setPath("testPath");

        HttpMethod method = transport.createHttpMethod(requestMessage);

        assertTrue("Method type is incorret", method instanceof GetMethod);
        assertEquals("Path is incorrect", "/testPath", method.getPath());
    }

    @Test
    public void testGetRequestPathGenerationWithContext() {
        HttpTransport transport = new HttpTransport();

        transport.establishConnection("localhost", 90);

        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMethod("get");
        requestMessage.setPath("testPath");
        requestMessage.setContext("context");

        HttpMethod method = transport.createHttpMethod(requestMessage);

        assertTrue("Method type is incorret", method instanceof GetMethod);
        assertEquals("Path is incorrect", "/context/testPath", method.getPath());
    }

    @Test
    public void testPutRequestPathGeneration() {
        HttpTransport transport = new HttpTransport();

        transport.establishConnection("localhost", 90);

        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMethod("PUT");
        requestMessage.setPath("testPath");

        HttpMethod method = transport.createHttpMethod(requestMessage);

        assertTrue("Method type is incorret", method instanceof PutMethod);
        assertEquals("Path is incorrect", "/testPath", method.getPath());
    }

    @Test
    public void testPutRequestPathGenerationWithContext() {
        HttpTransport transport = new HttpTransport();

        transport.establishConnection("localhost", 90);

        RequestMessage requestMessage = new RequestMessage();

        requestMessage.setMethod("PUT");
        requestMessage.setPath("testPath");
        requestMessage.setContext("context");

        HttpMethod method = transport.createHttpMethod(requestMessage);

        assertTrue("Method type is incorret", method instanceof PutMethod);
        assertEquals("Path is incorrect", "/context/testPath", method.getPath());
    }

}
