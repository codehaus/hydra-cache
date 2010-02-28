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
package org.hydracache.server.httpd.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author nzhu
 * 
 */
public class AbstractHttpMethodHandlerTest {

    @Mock
    protected HarmonyDataBank mockDataBank;
    @Mock
    protected HttpEntityEnclosingRequest mockRequest;
    @Mock
    protected HttpResponse mockResponse;
    @Mock
    protected HttpContext mockHttpContext;

    public AbstractHttpMethodHandlerTest() {
        super();
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    protected void stubGetRequestURI(HttpRequest mockRequest, String requestUri) {
        RequestLine mockRequestLine = mock(RequestLine.class);
        when(mockRequest.getRequestLine()).thenReturn(mockRequestLine);
        when(mockRequestLine.getUri()).thenReturn(requestUri);
    }

    protected void stubGetProtocolParam(String protocol) {
        HttpParams mockHttpParams = mock(HttpParams.class);
        when(mockRequest.getParams()).thenReturn(mockHttpParams);
        when(
                mockHttpParams
                        .getParameter(BaseHttpMethodHandler.PROTOCOL_PARAMETER_NAME))
                .thenReturn(protocol);
    }

}