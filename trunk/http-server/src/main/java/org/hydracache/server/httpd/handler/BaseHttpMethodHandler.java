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

import static org.hydracache.server.httpd.HttpConstants.SLASH;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Abstract base class for method based request handler
 * 
 * @author nzhu
 * 
 */
public abstract class BaseHttpMethodHandler implements HttpRequestHandler {

    protected HarmonyDataBank dataBank;

    public BaseHttpMethodHandler(HarmonyDataBank dataBank) {
        this.dataBank = dataBank;
    }

    protected Long extractDataKeyHash(HttpRequest request) {
        return extractDataKeyHash(getRequestUri(request));
    }

    protected Long extractDataKeyHash(final String requestUri) {
        String requestContext = extractRequestContext(requestUri);

        Validate.isTrue(NumberUtils.isNumber(requestContext), "Data key hash["
                + requestContext + "] is not a number");

        return Long.valueOf(requestContext);
    }

    protected String getRequestUri(HttpRequest request) {
        String requestUri = request.getRequestLine().getUri();

        return requestUri;
    }

    protected String extractRequestContext(final HttpRequest request) {
        return extractRequestContext(getRequestUri(request));
    }

    protected String extractRequestContext(final String requestUri) {
        String cleanUri = StringUtils.trim(requestUri);

        if (cleanUri.endsWith(SLASH)) {
            cleanUri = StringUtils.chop(cleanUri);
        }

        String requestContext = StringUtils.substringAfterLast(cleanUri, SLASH);

        return requestContext;
    }

    protected boolean hashKeyDoesNotExist(HttpRequest request) {
        return !NumberUtils.isNumber(extractRequestContext(request));
    }

}