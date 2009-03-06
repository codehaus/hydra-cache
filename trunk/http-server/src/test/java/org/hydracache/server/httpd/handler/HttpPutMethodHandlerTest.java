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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;

import net.sf.ehcache.CacheManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.hydracache.io.Buffer;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.BlobDataMessage;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.resolver.SyntacticReconciliationResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.EhcacheDataBank;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpPutMethodHandlerTest {
    private final Mockery context = new Mockery();

    private final HttpResponse response = context.mock(HttpResponse.class);

    private final HttpContext httpContext = context.mock(HttpContext.class);

    private IncrementVersionFactory versionFactoryMarshaller;

    private HttpPutMethodHandler handler;

    @Before
    public void initialize() {
        versionFactoryMarshaller = new IncrementVersionFactory();
        handler = createHandler(versionFactoryMarshaller);
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());
    }

    @Test
    public void shouldReturnStatus201ForCreation() throws Exception {
        final DataBank dataBank = new EhcacheDataBank(new SyntacticReconciliationResolver(),
                CacheManager.create());
        
        handler.dataBank = dataBank;
        
        int statusCode = handler.createStatusCode(1000L);

        assertEquals("Status for creation is incorrect", HttpStatus.SC_CREATED,
                statusCode);
    }
    
    @Test
    public void shouldReturnStatus200ForUpdate() throws Exception {
        final DataBank dataBank = new EhcacheDataBank(new SyntacticReconciliationResolver(),
                CacheManager.create());
        
        handler.dataBank = dataBank;
        
        long dataKey = 1000L;
        
        dataBank.put(new Data(dataKey));
        
        int statusCode = handler.createStatusCode(dataKey);

        assertEquals("Status for update is incorrect", HttpStatus.SC_OK,
                statusCode);
    }

    @Test
    public void testEmptyRequestShouldBeRejected() throws HttpException,
            IOException {
        final HttpRequest emptyRequest = context.mock(HttpRequest.class);
        handler.handle(emptyRequest, response, httpContext);
        handler.handle(null, response, httpContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProtocolMessageTypeVerification() {
        handler.verifyMessageType(context.mock(DataMessage.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHashKeyConsistencyVerification()
            throws UnknownHostException {

        final BlobDataMessage dataMessage = createExpectedDataMsg();

        handler.verifyKeyHashConsistency(dataMessage,
                dataMessage.getKeyHash() + 1);
    }

    @Test
    public void testDecodeBlobDataMessage() throws IOException {
        final BlobDataMessage expectedMessage = createExpectedDataMsg();

        final Buffer buffer = encodeMessageToBuffer(expectedMessage);

        final HttpEntity entity = context.mock(HttpEntity.class);

        {
            addReturnInputStreamExpectation(buffer, entity);

            addGetContentLengthExpectation(buffer, entity);
        }

        final BlobDataMessage decodedMessage = handler
                .decodeProtocolMessage(entity);

        assertEquals("Decoded message is incorrect", expectedMessage,
                decodedMessage);
    }

    private void addGetContentLengthExpectation(final Buffer buffer,
            final HttpEntity entity) {
        context.checking(new Expectations() {
            {
                atLeast(1).of(entity).getContentLength();
                will(returnValue(new Long(buffer.size())));
            }
        });
    }

    private void addReturnInputStreamExpectation(final Buffer buffer,
            final HttpEntity entity) throws IOException {
        context.checking(new Expectations() {
            {
                one(entity).getContent();
                will(returnValue(buffer.asDataInputStream()));
            }
        });
    }

    private Buffer encodeMessageToBuffer(final BlobDataMessage expectedMessage)
            throws IOException {
        final Buffer buffer = Buffer.allocate();

        new DefaultProtocolEncoder(new MessageMarshallerFactory(
                versionFactoryMarshaller)).encode(expectedMessage, buffer
                .asDataOutpuStream());

        return buffer;
    }

    private BlobDataMessage createExpectedDataMsg() throws UnknownHostException {
        final BlobDataMessage expectedMessage = new BlobDataMessage();

        expectedMessage.setBlob(new byte[250]);
        expectedMessage.setKeyHash(1000);
        expectedMessage.setVersion(versionFactoryMarshaller
                .create(new Identity(1)));

        return expectedMessage;
    }

    private HttpPutMethodHandler createHandler(
            final Marshaller<Version> versionMarshaller) {
        final DataBank dataBank = context.mock(DataBank.class);

        final HttpPutMethodHandler handler = new HttpPutMethodHandler(dataBank,
                new DefaultProtocolDecoder(new MessageMarshallerFactory(
                        versionMarshaller)));

        return handler;
    }
}
