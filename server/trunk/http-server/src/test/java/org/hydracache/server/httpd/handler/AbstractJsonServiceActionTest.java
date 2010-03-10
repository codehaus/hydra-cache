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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 *
 */
public class AbstractJsonServiceActionTest {

    @Mock
    protected HttpResponse mockResponse;
    @Mock
    protected HttpRequest mockRequest;

    /**
     * 
     */
    public AbstractJsonServiceActionTest() {
        super();
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    protected void stubRequestWithEmptyParams() {
        mockRequest = null;
    }

    protected void stubJSonPHandlerParam(String jsonHandlerName) {
        HttpParams httpParams = mock(HttpParams.class);
        when(mockRequest.getParams()).thenReturn(httpParams);
        when(httpParams.getParameter("handler")).thenReturn(jsonHandlerName);
    }

}