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

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Get http method handler
 * 
 * @author nzhu
 * 
 */
public class HttpGetMethodHandler extends BaseHttpMethodHandler {
    private static Logger log = Logger.getLogger(HttpGetMethodHandler.class);

    private Map<String, HttpServiceAction> serviceActionMap = Collections
            .emptyMap();

    /**
     * Constructor
     */
    public HttpGetMethodHandler(HarmonyDataBank dataBank,
            HashFunction hashFunction,
            ProtocolEncoder<DataMessage> messageEncoder) {
        super(dataBank, hashFunction, messageEncoder);
    }

    public synchronized void setServiceActions(Set<HttpServiceAction> actions) {
        Map<String, HttpServiceAction> tmpActionMap = new HashMap<String, HttpServiceAction>();

        for (HttpServiceAction httpServiceAction : actions) {
            tmpActionMap.put(httpServiceAction.getName(), httpServiceAction);
        }

        serviceActionMap = Collections.unmodifiableMap(tmpActionMap);
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
        if (keyIsBlank(request))
            return;

        String requestContext = extractRequestString(request);

        if (serviceActionMap.containsKey(requestContext)) {
            HttpServiceAction action = serviceActionMap.get(requestContext);
            action.execute(response);
        } else {
            handleGetData(request, response);
        }
    }

    private void handleGetData(HttpRequest request, HttpResponse response)
            throws IOException {
        Long dataKey = extractDataKeyHash(request);

        Data data = dataBank.get(dataKey);

        if (data == null) {
            handleNotFound(response);
            return;
        }

        ByteArrayEntity body = generateEntityForData(data);
        response.setEntity(body);
        response.setStatusCode(HttpStatus.SC_OK);
    }

    private void handleNotFound(HttpResponse response) {
        response.setStatusCode(HttpStatus.SC_NOT_FOUND);
        try {
            StringEntity body = new StringEntity("Data not found");
            body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);
            response.setEntity(body);
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to generate response: ", e);
        }
    }

}
