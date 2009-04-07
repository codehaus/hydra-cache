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
package org.hydracache.client.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.lang.SerializationUtils;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.protocol.util.ProtocolUtils;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.hydracache.server.harmony.core.SubstancePartition;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class PartitionAwareClientTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private PartitionAwareClient client;

    private Identity defaultIdentity;

    private IncrementVersionFactory versionMarshaller;

    private DefaultProtocolEncoder defaultProtocolEncoder;

    private DefaultProtocolDecoder defaultProtocolDecoder;

    private String testKey = "testKey";

    @Before
    public void beforeTests() throws Exception {
        this.defaultIdentity = new Identity(8080);
        List<Identity> ids = Collections.singletonList(defaultIdentity);
        SubstancePartition partition = new SubstancePartition(
                new KetamaBasedHashFunction(), ids);
        client = new PartitionAwareClient(partition);

        versionMarshaller = new IncrementVersionFactory();
        versionMarshaller.setIdentityMarshaller(new IdentityMarshaller());
        defaultProtocolEncoder = new DefaultProtocolEncoder(
                new MessageMarshallerFactory(versionMarshaller));
        defaultProtocolDecoder = new DefaultProtocolDecoder(
                new MessageMarshallerFactory(versionMarshaller));
    }

    @Test
    public void ensureOkAndCreatedStatusCodeAreAcceptableInGet()
            throws Exception {
        client.validateGetResponseCode(HttpStatus.SC_OK);
        client.validateGetResponseCode(HttpStatus.SC_CREATED);
    }

    @Test(expected = IOException.class)
    public void ensureNotFoundTriggersExceptionInGet() throws Exception {
        client.validateGetResponseCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void ensureOkAndCreatedStatusCodeAreAcceptableInPut()
            throws Exception {
        client.validatePutResponseCode(HttpStatus.SC_OK);
        client.validatePutResponseCode(HttpStatus.SC_CREATED);
    }

    @Test(expected = VersionConflictException.class)
    public void ensureConflictTriggersExceptionInPut() throws Exception {
        client.validatePutResponseCode(HttpStatus.SC_CONFLICT);
    }

    @Test(expected = IOException.class)
    public void ensureNotFoundTriggersExceptionInPut() throws Exception {
        client.validatePutResponseCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void ensureVersionUpdateAfterPutMethod() throws Exception {
        String expectedResult = "Test Message";

        final DataMessage dataMsg = new DataMessage();
        dataMsg.setBlob(SerializationUtils.serialize(expectedResult));
        dataMsg.setVersion(versionMarshaller.create(defaultIdentity));

        final PutMethod putMethod = context.mock(PutMethod.class);

        context.checking(new Expectations() {
            {
                one(putMethod).getResponseBodyAsStream();
                will(returnValue(ProtocolUtils.encodeDataMessage(
                        defaultProtocolEncoder, dataMsg).asDataInputStream()));
            }
        });

        client.retrievePutResponse(testKey, putMethod);

        assertEquals("Version should be updated", dataMsg.getVersion(),
                client.versionMap.get(testKey));
    }

    @Test
    public void ensureResultRetrievalFromGetMethod() throws Exception {
        String expectedResult = "Test Message";

        final DataMessage dataMsg = new DataMessage();
        dataMsg.setBlob(SerializationUtils.serialize(expectedResult));
        dataMsg.setVersion(versionMarshaller.create(defaultIdentity));

        final GetMethod getMethod = context.mock(GetMethod.class);

        context.checking(new Expectations() {
            {
                one(getMethod).getResponseBodyAsStream();
                will(returnValue(ProtocolUtils.encodeDataMessage(
                        defaultProtocolEncoder, dataMsg).asDataInputStream()));
            }
        });

        Object object = client.getReturnedObject(testKey, getMethod);

        Object result = object;

        assertEquals("Result retrieved from GET method is incorrect",
                expectedResult, result);
    }

    @Test
    public void ensureRequestEntityIsBuiltCorrect() throws IOException {
        Serializable data = "Test Message";

        RequestEntity requestEntity = client.buildRequestEntity(testKey, data,
                defaultIdentity);

        assertNotNull("Request entity is null", requestEntity);

        Buffer buffer = Buffer.allocate();

        requestEntity.writeRequest(buffer.asDataOutpuStream());

        DataMessage msg = ProtocolUtils.decodeProtocolMessage(
                defaultProtocolDecoder, buffer.toByteArray());

        assertEquals("Request entity blob content is incorrect", data,
                SerializationUtils.deserialize(msg.getBlob()));
    }

    @Test
    public void ensureUrlConstructionCorrectness() {
        String key = "session893475";
        Identity localhostId = new Identity(8080);

        String url = client.constructUri(key, localhostId);

        assertEquals("URL construction is incorrect", "http://"
                + localhostId.getAddress().getHostName() + ":8080/" + key, url);
    }
}
