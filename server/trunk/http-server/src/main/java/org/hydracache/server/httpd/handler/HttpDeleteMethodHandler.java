/*
 * Copyright 2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hydracache.server.httpd.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Delete http method handler
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class HttpDeleteMethodHandler extends BaseHttpMethodHandler {

    /**
     * Constructor
     */
    public HttpDeleteMethodHandler(HarmonyDataBank dataBank,
            HashFunction hashFunction,
            ProtocolEncoder<DataMessage> messageEncoder) {
        super(dataBank, hashFunction, messageEncoder);
    }

    public synchronized void setServiceActions(Set<HttpServiceAction> actions) {
        Map<String, HttpServiceAction> tmpActionMap = new HashMap<String, HttpServiceAction>();

        for (HttpServiceAction httpServiceAction : actions) {
            tmpActionMap.put(httpServiceAction.getName(), httpServiceAction);
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
        if (keyIsBlank(request))
            return;

        handleDelete(request, response);
    }

    private void handleDelete(HttpRequest request, HttpResponse response)
            throws IOException {
        Long dataKey = extractDataKeyHash(request);
        String storageContext = extractRequestContext(request);

        dataBank.delete(storageContext, dataKey);

        response.setStatusCode(HttpStatus.SC_OK);
    }

}
