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

import org.apache.commons.lang.Validate;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.ProtocolDecoder;
import org.hydracache.protocol.data.message.BlobDataMessage;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Put http method request handler
 * 
 * @author nzhu
 * 
 */
public class HttpPutMethodHandler extends BaseHttpMethodHandler {
    private static Logger log = Logger.getLogger(HttpPutMethodHandler.class);

    private ProtocolDecoder<DataMessage> decoder;

    /**
     * Constructor
     */
    public HttpPutMethodHandler(HarmonyDataBank dataBank,
            ProtocolDecoder<DataMessage> decoder) {
        super(dataBank);
        this.decoder = decoder;
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

        if (isEmpty(request)) {
            log.warn("Empty request[" + request + "] received and ignored");
            return;
        }
        
        if (hashKeyDoesNotExist(request))
            return;

        HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();

        BlobDataMessage dataMessage = decodeProtocolMessage(entity);

        Long dataKey = extractDataKeyHash(request);

        verifyKeyHashConsistency(dataMessage, dataKey);

        int statusCode = createStatusCode(dataKey);

        doPut(response, dataMessage);

        response.setStatusCode(statusCode);
    }

    int createStatusCode(Long dataKey) throws IOException {
        int returnStatusCode = HttpStatus.SC_OK;

        if (dataBank.getLocally(dataKey) == null)
            returnStatusCode = HttpStatus.SC_CREATED;
        
        return returnStatusCode;
    }

    private void doPut(HttpResponse response, BlobDataMessage dataMessage) {
        try {
            dataBank.put(new Data(dataMessage.getKeyHash(), dataMessage
                    .getVersion(), dataMessage.getBlob()));
        } catch (IOException ex) {
            log.error("Failed to put:", ex);
            response.setStatusCode(HttpStatus.SC_METHOD_FAILURE);
        }
    }

    void verifyKeyHashConsistency(BlobDataMessage dataMessage, Long dataKey) {
        Validate.isTrue(dataKey.equals(dataMessage.getKeyHash()),
                "Data key hash does not match the hash specified in the URL");
    }

    BlobDataMessage decodeProtocolMessage(HttpEntity entity) throws IOException {
        byte[] entityContent = EntityUtils.toByteArray(entity);

        if (log.isDebugEnabled()) {
            log.debug("Incoming entity content (bytes): "
                    + entityContent.length);
        }

        DataMessage dataMessage = decoder.decode(Buffer.wrap(entityContent)
                .asDataInputStream());

        verifyMessageType(dataMessage);

        return (BlobDataMessage) dataMessage;
    }

    void verifyMessageType(DataMessage dataMessage) {
        Validate.isTrue(dataMessage instanceof BlobDataMessage,
                "Unsupported protocol message[" + dataMessage + "] received");
    }

    private boolean isEmpty(HttpRequest request) {
        return !(request instanceof HttpEntityEnclosingRequest);
    }

}
