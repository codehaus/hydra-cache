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
package org.hydracache.server.httpd;

import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.protocol.BufferingHttpServiceHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

/**
 * @author nzhu
 * 
 */
public class HttpServiceHandlerFactory {
    private HttpParams httpParams;

    private HttpRequestHandler requestHandler;

    private EventListener protocolEventListener;

    public HttpServiceHandlerFactory(HttpParams httpParams,
            HttpRequestHandler requestHandler,
            EventListener protocolEventListener) {
        this.httpParams = httpParams;
        this.requestHandler = requestHandler;
        this.protocolEventListener = protocolEventListener;
    }

    public NHttpServiceHandler create() throws Exception {
        BasicHttpProcessor httpproc = new BasicHttpProcessor();

        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());

        BufferingHttpServiceHandler handler = new BufferingHttpServiceHandler(
                httpproc, new DefaultHttpResponseFactory(),
                new DefaultConnectionReuseStrategy(), httpParams);

        HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();

        reqistry.register("*", requestHandler);

        handler.setHandlerResolver(reqistry);

        handler.setEventListener(protocolEventListener);

        return handler;
    }

}
