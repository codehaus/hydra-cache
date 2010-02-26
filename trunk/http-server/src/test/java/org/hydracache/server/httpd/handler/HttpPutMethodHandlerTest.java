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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.marshaller.DataMessageXmlMarshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.IdentityXmlMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpPutMethodHandlerTest extends AbstractHttpMethodHandlerTest {
    @Test
    public void ensureLocalVersionConflictDetectionIgnoresNull()
            throws Exception {
        HttpPutMethodHandler handler = createHttpPutHandler();

        stubGetRequestURI(mockRequest, "/testContext/testKey");
        stubGetByteArrayEntityFromRequest(createValidNewMessageInBytes());
        stubNotFoundLocalGet(mockDataBank);

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockDataBank).put(anyString(), any(Data.class));
        verify(mockResponse).setStatusCode(HttpStatus.SC_CREATED);
        verify(mockResponse).setEntity(any(ByteArrayEntity.class));

    }

    private void stubNotFoundLocalGet(HarmonyDataBank mockDataBank) throws IOException {
        when(mockDataBank.getLocally(anyString(), anyLong())).thenReturn(null);
    }

    private void stubGetByteArrayEntityFromRequest(byte[] data) {
        when(mockRequest.getEntity()).thenReturn(new ByteArrayEntity(data));
    }

    private HttpPutMethodHandler createHttpPutHandler() {
        HashFunction hashFunction = new KetamaBasedHashFunction();

        IncrementVersionFactory versionMarshaller = new IncrementVersionFactory(
                new IdentityMarshaller());

        DefaultProtocolEncoder messageEncoder = createMessageEncoder(versionMarshaller);

        DefaultProtocolDecoder messageDecoder = new DefaultProtocolDecoder(
                new DataMessageMarshaller(versionMarshaller),
                new DataMessageXmlMarshaller(new VersionXmlMarshaller(
                        new IdentityXmlMarshaller(), versionMarshaller)));

        final HttpPutMethodHandler handler = new HttpPutMethodHandler(
                versionMarshaller, mockDataBank, hashFunction, messageEncoder,
                messageDecoder, new JGroupsNode(new Identity(7070),
                        new IpAddress(7000)));

        return handler;
    }

    private DefaultProtocolEncoder createMessageEncoder(
            IncrementVersionFactory versionMarshaller) {
        DefaultProtocolEncoder messageEncoder = new DefaultProtocolEncoder(
                new DataMessageMarshaller(versionMarshaller),
                new DataMessageXmlMarshaller(new VersionXmlMarshaller(
                        new IdentityXmlMarshaller(), versionMarshaller)));
        return messageEncoder;
    }

    private byte[] createValidNewMessageInBytes() throws IOException {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob("test data".getBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory().createNull());
        DataMessage message = incomingDataMsg;
        return encodeMessage(message);
    }

    private byte[] encodeMessage(DataMessage message) throws IOException {
        IncrementVersionFactory versionMarshaller = new IncrementVersionFactory(
                new IdentityMarshaller());
        DefaultProtocolEncoder messageEncoder = createMessageEncoder(versionMarshaller);
        Buffer buffer = Buffer.allocate();
        messageEncoder.encode(message, buffer.asDataOutpuStream());
        byte[] bytes = buffer.toByteArray();
        return bytes;
    }

    @Test
    public void ensureLocalVersionConflictDetection() throws Exception {
        HttpPutMethodHandler handler = createHttpPutHandler();

        stubGetRequestURI(mockRequest, "/testContext/testKey");
        stubGetByteArrayEntityFromRequest(createValidNewMessageInBytes());
        
        Data data = new Data();
        data.setVersion(new IncrementVersionFactory().create(new Identity(4040))
                .incrementFor(new Identity(7070)));
        when(mockDataBank.getLocally(anyString(), anyLong())).thenReturn(data);

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockDataBank, never()).put(anyString(), any(Data.class));
        verify(mockResponse).setStatusCode(HttpStatus.SC_CONFLICT);
        verify(mockResponse).setEntity(any(StringEntity.class));
    }

    @Test
    public void ensureNewVersionIsCreatedIfGivenVersionIsNull()
            throws Exception {
        HttpPutMethodHandler handler = createHttpPutHandler();

        stubGetRequestURI(mockRequest, "/testContext/testKey");
        stubGetByteArrayEntityFromRequest(createValidNewMessageInBytes());
        stubNotFoundLocalGet(mockDataBank);

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockDataBank).put(anyString(), any(Data.class));
        verify(mockResponse).setStatusCode(HttpStatus.SC_CREATED);
        verify(mockResponse).setEntity(any(ByteArrayEntity.class));
    }

    @Test
    public void ensureStatusOkIsReturnedForUpdate() throws Exception {
        HttpPutMethodHandler handler = createHttpPutHandler();

        stubGetRequestURI(mockRequest, "/testContext/testKey");
        stubGetByteArrayEntityFromRequest(createValidUpdateDataMessageInBytes());
        Data data = new Data();
        data.setVersion(new IncrementVersionFactory().create(new Identity(4040)));
        when(mockDataBank.getLocally(anyString(), anyLong())).thenReturn(data);

        handler.handle(mockRequest, mockResponse, mockHttpContext);

        verify(mockDataBank).put(anyString(), any(Data.class));
        verify(mockResponse).setStatusCode(HttpStatus.SC_OK);
        verify(mockResponse).setEntity(any(ByteArrayEntity.class));
    }

    private byte[] createValidUpdateDataMessageInBytes() throws IOException {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob("test data".getBytes());
        incomingDataMsg.setVersion(new IncrementVersionFactory()
                .create(new Identity(4040)).incrementFor(new Identity(7070)));
        DataMessage message = incomingDataMsg;
        return encodeMessage(message);
    }

    @Test
    public void ensureBlankKeyIsRejected() throws HttpException, IOException {
        HttpPutMethodHandler handler = createHttpPutHandler();

        stubGetRequestURI(mockRequest, "/");
        
        handler.handle(mockRequest, mockResponse, mockHttpContext);
    }

}
