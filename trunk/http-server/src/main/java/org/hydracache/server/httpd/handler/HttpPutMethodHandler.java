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

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.protocol.data.codec.ProtocolDecoder;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.protocol.util.ProtocolUtils;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.hydracache.server.harmony.core.Node;
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

    private Node localNode;

    /**
     * Constructor
     */
    public HttpPutMethodHandler(HarmonyDataBank dataBank,
            HashFunction hashFunction, ProtocolDecoder<DataMessage> decoder,
            Node localNode) {
        super(dataBank, hashFunction);
        this.decoder = decoder;
        this.localNode = localNode;
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

        if (emptyRequest(request)) {
            log.warn("Empty request[" + request + "] received and ignored");
            return;
        }

        if (keyIsBlank(request))
            return;

        HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
        DataMessage dataMessage = decodeProtocolMessage(entity);
        increaseVersion(dataMessage);
        Long dataKey = extractDataKeyHash(request);

        try {
            guardConflictWithLocalDataVersion(dataKey, dataMessage);

            int statusCode = createStatusCode(dataKey);

            doPut(response, dataKey, dataMessage);

            response.setStatusCode(statusCode);
        } catch (VersionConflictException vce) {
            handleVersionConflictException(response, vce);
        } 
    }

    private boolean emptyRequest(HttpRequest request) {
        return !(request instanceof HttpEntityEnclosingRequest);
    }

    DataMessage decodeProtocolMessage(HttpEntity entity) throws IOException {
        byte[] entityContent = EntityUtils.toByteArray(entity);

        return ProtocolUtils.decodeProtocolMessage(decoder, entityContent);
    }

    private void increaseVersion(DataMessage dataMessage) {
        dataMessage.setVersion(dataMessage.getVersion().incrementFor(
                localNode.getId()));
    }

    void guardConflictWithLocalDataVersion(Long dataKey,
            DataMessage incomingDataMsg) throws IOException,
            VersionConflictException {
        Data localData = dataBank.getLocally(dataKey);

        if (localData != null) {
            Version existingVersion = localData.getVersion();
            Version newVersion = incomingDataMsg.getVersion();

            if (!newVersion.isDescendantOf(existingVersion)) {
                throw new VersionConflictException(
                        "Version conflict detected between existing["
                                + existingVersion + "] and new[" + newVersion
                                + "]");
            }
        }
    }

    int createStatusCode(Long dataKey) throws IOException {
        int returnStatusCode = HttpStatus.SC_OK;

        if (dataBank.getLocally(dataKey) == null)
            returnStatusCode = HttpStatus.SC_CREATED;

        return returnStatusCode;
    }

    private void doPut(HttpResponse response, Long dataKey,
            DataMessage dataMessage) throws IOException,
            VersionConflictException {
        dataBank.put(new Data(dataKey, dataMessage.getVersion(), dataMessage
                .getBlob()));
    }

    private void handleVersionConflictException(HttpResponse response,
            VersionConflictException vce) throws UnsupportedEncodingException {
        log.debug("Version conflict:" + vce.getMessage());
        response.setStatusCode(HttpStatus.SC_CONFLICT);
        StringEntity body = new StringEntity(vce.getMessage());
        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);
        response.setEntity(body);
    }

}
