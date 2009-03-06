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

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.message.BlobDataMessage;
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

    private ProtocolEncoder<DataMessage> messageEncoder;

    private HttpGetAction printRegistryAction;

    private HttpGetAction printStorageInfoAction;

    private HttpGetAction storageDumpAction;

    /**
     * Constructor
     */
    public HttpGetMethodHandler(HarmonyDataBank dataBank,
            ProtocolEncoder<DataMessage> messageEncoder) {
        super(dataBank);
        this.messageEncoder = messageEncoder;
    }

    public void setPrintRegistryAction(HttpGetAction getRegistryHandler) {
        this.printRegistryAction = getRegistryHandler;
    }

    public void setPrintStorageInfoAction(HttpGetAction printStorageInfoAction) {
        this.printStorageInfoAction = printStorageInfoAction;
    }

    public void setStorageDumpAction(HttpGetAction storageDumpAction) {
        this.storageDumpAction = storageDumpAction;
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
        String requestContext = extractRequestContext(request);

        if (printRegistryAction.getName().equals(requestContext)) {
            printRegistryAction.execute(response);
        } else if (printStorageInfoAction.getName().equals(requestContext)) {
            printStorageInfoAction.execute(response);
        } else if (storageDumpAction.getName().equals(requestContext)) {
            storageDumpAction.execute(response);
        } else {
            handleGetData(request, response);
        }
    }

    void handleGetData(HttpRequest request, HttpResponse response)
            throws IOException {
        if (hashKeyDoesNotExist(request))
            return;

        Long dataKey = extractDataKeyHash(request);

        Data data = dataBank.get(dataKey);

        Buffer buffer = serializeDataMessage(data);

        ByteArrayEntity body = new ByteArrayEntity(buffer.toByteArray());

        body.setContentType(BINARY_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

    Buffer serializeDataMessage(Data data) throws IOException {
        Buffer buffer = Buffer.allocate();

        BlobDataMessage msg = new BlobDataMessage();

        if (data != null) {
            msg.setVersion(data.getVersion());
            msg.setKeyHash(data.getKeyHash());
            msg.setBlob(data.getContent());

            messageEncoder.encode(msg, buffer.asDataOutpuStream());
        }

        return buffer;
    }

}
