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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionFactory;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpPutMethodHandlerTest {
    private Mockery context;

    private HttpResponse response;

    private IncrementVersionFactory versionFactoryMarshaller;

    private HashFunction hashFunction = new KetamaBasedHashFunction();

    private HttpPutMethodHandler handler;

    private Long testKey = 1234L;

    private Identity sourceId = new Identity(70);
    private Identity localId = new Identity(71);

    @Before
    public void initialize() {
        context = new Mockery() {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };

        response = context.mock(HttpResponse.class);

        versionFactoryMarshaller = new IncrementVersionFactory();
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());

        handler = createHandler(versionFactoryMarshaller,
                versionFactoryMarshaller);
    }

    private HttpPutMethodHandler createHandler(
            final VersionFactory versionFactory,
            final Marshaller<Version> versionMarshaller) {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        DefaultProtocolEncoder messageEncoder = new DefaultProtocolEncoder(
                new MessageMarshallerFactory(versionFactoryMarshaller));
        DefaultProtocolDecoder messageDecoder = new DefaultProtocolDecoder(
                new MessageMarshallerFactory(versionMarshaller));

        final HttpPutMethodHandler handler = new HttpPutMethodHandler(
                versionFactory, dataBank, hashFunction, messageEncoder,
                messageDecoder, new JGroupsNode(localId, new IpAddress(7000)));

        return handler;
    }

    @After
    public void after() {
        context.assertIsSatisfied();
    }

    @Test
    public void ensureLocalVersionConflictDetectionIgnoresNull()
            throws Exception {
        {
            addNullReturnedFromLocalGetExp(handler.dataBank);
            addSuccessLocalPutExp(handler.dataBank);
        }

        {
            addSetCreatedStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        DataMessage validNewDataMessage = createValidNewDataMessage();

        handler.processDataMessage(response, testKey, validNewDataMessage);
    }

    private void addNullReturnedFromLocalGetExp(final HarmonyDataBank dataBank)
            throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(dataBank).getLocally(with(testKey));
                will(returnValue(null));
            }
        });
    }

    private void addSuccessLocalPutExp(final HarmonyDataBank dataBank)
            throws Exception {
        context.checking(new Expectations() {
            {
                one(dataBank).put(with(any(Data.class)));
            }
        });
    }

    private void addSetCreatedStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_CREATED);
            }
        });
    }

    private void addSetBinaryDataEntityExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(ByteArrayEntity.class)));
            }
        });
    }

    private DataMessage createValidNewDataMessage() {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(TestDataGenerator.createRandomData()
                .getContent());
        incomingDataMsg.setVersion(new IncrementVersionFactory().createNull());
        return incomingDataMsg;
    }

    @Test
    public void ensureLocalVersionConflictDetection() throws Exception {
        {
            addConflictLocalGetExp(handler.dataBank);
        }

        {
            addSetConflictStatusCodeExp(response);
            addSetStringMessageEntityExp(response);
        }

        DataMessage validNewDataMessage = createValidNewDataMessage();

        handler.processDataMessage(response, testKey, validNewDataMessage);
    }

    private void addConflictLocalGetExp(final HarmonyDataBank dataBank)
            throws IOException {
        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                Data data = new Data();
                data.setVersion(new IncrementVersionFactory().create(sourceId)
                        .incrementFor(localId));
                will(returnValue(data));
            }
        });
    }

    private void addSetConflictStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_CONFLICT);
            }
        });
    }

    private void addSetStringMessageEntityExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(StringEntity.class)));
            }
        });
    }

    @Test
    public void ensureNewVersionIsCreatedIfGivenVersionIsNull()
            throws Exception {
        {
            addNullReturnedFromLocalGetExp(handler.dataBank);
            addSuccessLocalPutExp(handler.dataBank);
        }

        {
            addSetCreatedStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        DataMessage nullVersionDataMessage = createValidNewDataMessage();
        nullVersionDataMessage.setVersion(null);

        handler.processDataMessage(response, testKey, nullVersionDataMessage);
    }

    @Test
    public void ensureStatusOkIsReturnedForUpdate() throws Exception {
        {
            addExistingDataReturnedFromLocalGetExp(handler.dataBank);
            addSuccessLocalPutExp(handler.dataBank);
        }

        {
            addSetOkStatusCodeExp(response);
            addSetBinaryDataEntityExp(response);
        }

        DataMessage validUpdateDataMessage = createValidUpdateDataMessage();

        handler.processDataMessage(response, testKey, validUpdateDataMessage);
    }

    private void addExistingDataReturnedFromLocalGetExp(
            final HarmonyDataBank dataBank) throws IOException {
        context.checking(new Expectations() {
            {
                atLeast(1).of(dataBank).getLocally(with(any(Long.class)));
                Data data = new Data();
                data.setVersion(new IncrementVersionFactory().create(sourceId));
                will(returnValue(data));
            }
        });
    }

    private DataMessage createValidUpdateDataMessage() {
        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setBlob(TestDataGenerator.createRandomData()
                .getContent());
        incomingDataMsg.setVersion(new IncrementVersionFactory()
                .create(localId).incrementFor(localId));
        return incomingDataMsg;
    }

    private void addSetOkStatusCodeExp(final HttpResponse response) {
        context.checking(new Expectations() {
            {
                one(response).setStatusCode(HttpStatus.SC_OK);
            }
        });
    }

}
