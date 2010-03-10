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
package org.hydracache.server.httpd;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class ParameterFetchRequestIntercepterTest {
    private ParameterFetchRequestIntercepter intercepter = new ParameterFetchRequestIntercepter();

    private HttpRequest mockRequest = mock(HttpRequest.class);

    private HttpContext mockContext = mock(HttpContext.class);

    private HttpParams params = new BasicHttpParams();

    @Test
    public void ensureEncodedParametersCanBeFetched() throws Exception {

        stubRequestUri(mockRequest, "/testCtx/testKey?string=test+string");

        stubRequestParams(mockRequest, params);

        intercepter.process(mockRequest, mockContext);

        assertEquals("test string", params.getParameter("string"));
    }

    private void stubRequestParams(HttpRequest mockRequest, HttpParams params) {
        when(mockRequest.getParams()).thenReturn(params);
    }

    private void stubRequestUri(HttpRequest mockRequest, String requestUri) {
        RequestLine mockRequestLine = mock(RequestLine.class);
        when(mockRequest.getRequestLine()).thenReturn(mockRequestLine);
        when(mockRequestLine.getUri()).thenReturn(requestUri);
    }

    @Test
    public void ensurePlainParametersCanBeFetched() throws Exception {

        stubRequestUri(mockRequest, "/testCtx/testKey?string=test string");

        stubRequestParams(mockRequest, params);

        intercepter.process(mockRequest, mockContext);

        assertEquals("test string", params.getParameter("string"));
    }

    @Test
    public void ensureMultiParametersCanBeFetched() throws Exception {

        stubRequestUri(mockRequest,
                "/testCtx/testKey?string=testString&more=moreString");

        stubRequestParams(mockRequest, params);

        intercepter.process(mockRequest, mockContext);

        assertEquals("testString", params.getParameter("string"));
        assertEquals("moreString", params.getParameter("more"));
    }

    @Test
    public void ensureNoParametersDoesNotCauseProblem() throws Exception {
        stubRequestUri(mockRequest, "/testCtx/testKey");

        stubRequestParams(mockRequest, params);

        intercepter.process(mockRequest, mockContext);
    }

    @Test
    public void ensureMalformedParametersDoesNotCauseProblem() throws Exception {
        stubRequestUri(mockRequest, "/testCtx/testKey?");

        stubRequestParams(mockRequest, params);

        intercepter.process(mockRequest, mockContext);
    }

    @Test
    public void ensureMissingParametersDoesNotCauseProblem() throws Exception {
        stubRequestUri(mockRequest, "/testCtx/testKey?string");

        stubRequestParams(mockRequest, params);

        intercepter.process(mockRequest, mockContext);
    }

}
