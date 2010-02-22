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

import static org.hydracache.server.httpd.HttpConstants.BINARY_RESPONSE_CONTENT_TYPE;
import static org.hydracache.server.httpd.HttpConstants.SLASH;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpRequestHandler;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.protocol.util.ProtocolUtils;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Abstract base class for method based request handler
 * 
 * @author nzhu
 * 
 */
public abstract class BaseHttpMethodHandler implements HttpRequestHandler {

    /**
     * 
     */
    private static final String QUESTION_MARK = "?";

    protected HarmonyDataBank dataBank;

    protected HashFunction hashFunction;

    protected ProtocolEncoder<DataMessage> messageEncoder;

    public BaseHttpMethodHandler(HarmonyDataBank dataBank,
            HashFunction hashFunction,
            ProtocolEncoder<DataMessage> messageEncoder) {
        this.dataBank = dataBank;
        this.hashFunction = hashFunction;
        this.messageEncoder = messageEncoder;
    }

    protected boolean emptyRequest(HttpRequest request) {
        return !(request instanceof HttpEntityEnclosingRequest);
    }

    protected boolean keyIsBlank(HttpRequest request) {
        return StringUtils.isBlank(extractRequestString(request));
    }

    protected Long extractDataKeyHash(HttpRequest request) {
        return extractDataKeyHash(getRequestUri(request));
    }

    protected Long extractDataKeyHash(final String requestUri) {
        String requestString = extractRequestString(requestUri);

        return hashFunction.hash(requestString);
    }

    protected String getRequestUri(HttpRequest request) {
        String requestUri = request.getRequestLine().getUri();

        return requestUri;
    }

    protected String extractRequestString(final HttpRequest request) {
        return extractRequestString(getRequestUri(request));
    }

    protected String extractRequestString(final String requestUri) {
        String cleanUri = StringUtils.trim(requestUri);

        if (cleanUri.endsWith(SLASH)) {
            cleanUri = StringUtils.chop(cleanUri);
        }

        cleanUri = StringUtils.removeStart(cleanUri, SLASH);

        String requestString = cleanUri;

        if (StringUtils.contains(requestString, SLASH)) {
            requestString = StringUtils.substringAfter(requestString, SLASH);

            if (StringUtils.contains(requestString, QUESTION_MARK)) {
                requestString = StringUtils.substringBefore(requestString,
                        QUESTION_MARK);
            }
        }

        return requestString;
    }
    
    protected String extractRequestContext(final HttpRequest request) {
        return extractRequestContext(getRequestUri(request));
    }
    
    protected String extractRequestContext(final String requestUri) {
        String requestString = StringUtils.trim(requestUri);
        
        requestString = StringUtils.removeStart(requestString, SLASH);
        requestString = StringUtils.removeEnd(requestString, SLASH);
        
        if (StringUtils.contains(requestString, SLASH)) {
            requestString = StringUtils.substringBefore(requestString, SLASH);
        }else{
            requestString = "";
        }

        return requestString;
    }

    protected ByteArrayEntity generateEntityForData(Data data)
            throws IOException {
        Buffer buffer = ProtocolUtils.encodeDataMessage(messageEncoder, data);

        ByteArrayEntity body = new ByteArrayEntity(buffer.toByteArray());

        body.setContentType(BINARY_RESPONSE_CONTENT_TYPE);
        return body;
    }

}