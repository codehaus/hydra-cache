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

import org.apache.http.HttpException;
import org.hydracache.io.Buffer;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionFactory;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpPutMethodHandlerTest extends AbstractHttpMethodHandlerTest {
    private HttpPutMethodHandler handler;

    @Override
    public void initialize() {
        super.initialize();

        handler = createHandler(versionFactoryMarshaller,
                versionFactoryMarshaller);
    }

    private HttpPutMethodHandler createHandler(
            final VersionFactory versionFactory,
            final Marshaller<Version> versionMarshaller) {

        final HttpPutMethodHandler handler = new HttpPutMethodHandler(
                versionFactory, dataBank, hashFunction, messageEncoder,
                messageDecoder, new JGroupsNode(localId, new IpAddress(7000)));

        return handler;
    }

    @Test
    public void ensureLocalVersionConflictDetectionIgnoresNull()
            throws Exception {
        {
            addGetRequestLineExp(request, TEST_KEY_REQUEST_CTX);
            addGetEntityExp(createValidNewMessageInBytes());
        }

        {
            addNullReturnedFromLocalGetExp(dataBank);
            addSuccessLocalPutExp(dataBank);
        }

        {
            addSetCreatedStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        handler.handle(request, response, httpContext);
    }

    private byte[] createValidNewMessageInBytes() throws IOException {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(generateRandomBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory().createNull());
        DataMessage message = incomingDataMsg;
        return encodeMessage(message);
    }

    private byte[] encodeMessage(DataMessage message) throws IOException {
        Buffer buffer = Buffer.allocate();
        messageEncoder.encode(message, buffer.asDataOutpuStream());
        byte[] bytes = buffer.toByteArray();
        return bytes;
    }

    @Test
    public void ensureLocalVersionConflictDetection() throws Exception {
        {
            addGetRequestLineExp(request, TEST_KEY_REQUEST_CTX);
            addGetEntityExp(createValidNewMessageInBytes());
        }

        {
            addConflictLocalGetExp(dataBank);
        }

        {
            addSetConflictStatusCodeExp(response);
            addSetStringMessageEntityExp(response);
        }

        handler.handle(request, response, httpContext);
    }

    @Test
    public void ensureNewVersionIsCreatedIfGivenVersionIsNull()
            throws Exception {
        {
            addGetRequestLineExp(request, TEST_KEY_REQUEST_CTX);
            addGetEntityExp(createNullVersionMessageInBytes());
        }

        {
            addNullReturnedFromLocalGetExp(dataBank);
            addSuccessLocalPutExp(dataBank);
        }

        {
            addSetCreatedStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        handler.handle(request, response, httpContext);
    }

    private byte[] createNullVersionMessageInBytes() throws IOException {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(generateRandomBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory().createNull());
        DataMessage message = incomingDataMsg;
        message.setVersion(versionFactoryMarshaller.createNull());
        return encodeMessage(message);
    }

    @Test
    public void ensureStatusOkIsReturnedForUpdate() throws Exception {
        {
            addGetRequestLineExp(request, TEST_KEY_REQUEST_CTX);
            addGetEntityExp(createValidUpdateDataMessageInBytes());
        }

        {
            addSuccessfulLocalGetExp(dataBank);
            addSuccessLocalPutExp(dataBank);
        }

        {
            addSetOkStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        handler.handle(request, response, httpContext);
    }

    private byte[] createValidUpdateDataMessageInBytes() throws IOException {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(generateRandomBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory()
                .create(localId).incrementFor(localId));
        DataMessage message = incomingDataMsg;
        return encodeMessage(message);
    }

    @Test
    public void ensureBlankKeyIsRejected() throws HttpException, IOException {
        {
            addGetRequestLineExp(request, "/");
        }

        handler.handle(request, response, httpContext);
    }

}
