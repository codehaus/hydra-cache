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
package org.hydracache.server.httpd.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.log4j.Logger;
import org.hydracache.server.httpd.HttpMethod;

/**
 * Request dispatcher that delegates actual handling to the contained
 * {@link HttpRequestHandler} based on the HTTP method being used
 * 
 * @author nzhu
 * 
 */
public class MethodBasedRequestDispatcher implements HttpRequestHandler {

    private static Logger log = Logger
            .getLogger(MethodBasedRequestDispatcher.class);

    private Map<HttpMethod, HttpRequestHandler> requestHandlerMap = new HashMap<HttpMethod, HttpRequestHandler>();

    /**
     * Constructor
     */
    public MethodBasedRequestDispatcher(HttpGetMethodHandler httpGetMethodHandler,
            HttpPutMethodHandler httpPutMethodHandler,
            UnsupportedHttpMethodHandler unsupportedHttpMethodHandler) {
        disallowAllMethods(unsupportedHttpMethodHandler);

        enableMethod(HttpMethod.GET, httpGetMethodHandler);

        enableMethod(HttpMethod.PUT, httpPutMethodHandler);
    }

    private void enableMethod(HttpMethod method,
            HttpRequestHandler requestHandler) {
        requestHandlerMap.put(method, requestHandler);
    }

    private void disallowAllMethods(
            UnsupportedHttpMethodHandler unsupportedHttpMethodHandler) {
        HttpMethod[] allMethods = HttpMethod.values();

        for (int i = 0; i < allMethods.length; i++) {
            HttpMethod httpMethod = allMethods[i];
            requestHandlerMap.put(httpMethod, unsupportedHttpMethodHandler);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.
     * HttpRequest, org.apache.http.HttpResponse,
     * org.apache.http.protocol.HttpContext)
     */
    @Override
    public void handle(HttpRequest request, HttpResponse response,
            HttpContext context) throws HttpException, IOException {

        String httpMethodName = request.getRequestLine().getMethod()
                .toUpperCase(Locale.ENGLISH);

        dispatch(request, response, context, httpMethodName);

    }

    void dispatch(HttpRequest request, HttpResponse response,
            HttpContext context, String httpMethodName) {
        try {
            HttpMethod httpMethod = HttpMethod.valueOf(httpMethodName);

            requestHandlerMap.get(httpMethod)
                    .handle(request, response, context);
        } catch (Exception ex) {
            log.warn("Error occured while handling request", ex);
        }
    }

}
