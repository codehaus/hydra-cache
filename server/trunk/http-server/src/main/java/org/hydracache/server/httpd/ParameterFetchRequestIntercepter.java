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

import java.io.IOException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * A request interceptor that extract and fetch request GET parameters and set
 * them to the request params object
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class ParameterFetchRequestIntercepter implements HttpRequestInterceptor {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest
     * , org.apache.http.protocol.HttpContext)
     */
    @Override
    public void process(HttpRequest request, HttpContext context)
            throws HttpException, IOException {
        String uri = request.getRequestLine().getUri();

        String paramString = StringUtils.substringAfter(uri, "?");

        String[] paramPairs = StringUtils.split(paramString, "&");

        for (int i = 0; i < paramPairs.length; i++) {
            String p = paramPairs[i];

            String[] paramPair = StringUtils.split(p, "=");

            if (paramPair != null && paramPair.length == 2) {
                String paramName = paramPair[0];
                String paramValue = paramPair[1];

                request.getParams().setParameter(paramName,
                        URLDecoder.decode(paramValue, "UTF-8"));
            }
        }
    }

}
