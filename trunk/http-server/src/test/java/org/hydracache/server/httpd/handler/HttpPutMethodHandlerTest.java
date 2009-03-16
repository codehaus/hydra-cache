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

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionConflictException;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpPutMethodHandlerTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final HttpResponse response = context.mock(HttpResponse.class);

    private final HttpContext httpContext = context.mock(HttpContext.class);

    private IncrementVersionFactory versionFactoryMarshaller;

    private HashFunction hashFunction = new KetamaBasedHashFunction();

    private HttpPutMethodHandler handler;

    private Identity sourceId = new Identity(70);
    private Identity localId = new Identity(71);

    @Before
    public void initialize() {
        versionFactoryMarshaller = new IncrementVersionFactory();
        handler = createHandler(versionFactoryMarshaller);
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());
    }

    @Test
    public void ensureLocalVersionConflictDetectionIgnoresNull()
            throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class,
                "conflictLocalDataBank");

        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                will(returnValue(null));
            }
        });

        handler.dataBank = dataBank;

        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setVersion(new IncrementVersionFactory()
                .create(sourceId));

        handler.guardConflictWithLocalDataVersion(10L, incomingDataMsg);

        context.assertIsSatisfied();
    }

    @Test(expected = VersionConflictException.class)
    public void ensureLocalVersionConflictDetection() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class,
                "conflictLocalDataBank");

        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                Data data = new Data();
                data.setVersion(new IncrementVersionFactory().create(sourceId)
                        .incrementFor(localId));
                will(returnValue(data));
            }
        });

        handler.dataBank = dataBank;

        DataMessage incomingDataMsg = new DataMessage();
        incomingDataMsg.setVersion(new IncrementVersionFactory()
                .create(sourceId));

        handler.guardConflictWithLocalDataVersion(10L, incomingDataMsg);
    }

    @Test
    public void shouldReturnStatus201ForCreation() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class,
                "emptyDataBank");

        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                will(returnValue(null));
            }
        });

        handler.dataBank = dataBank;

        int statusCode = handler.createStatusCode(1000L);

        assertEquals("Status for creation is incorrect", HttpStatus.SC_CREATED,
                statusCode);
    }

    @Test
    public void shouldReturnStatus200ForUpdate() throws Exception {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class,
                "nonEmptyDataBank");

        context.checking(new Expectations() {
            {
                one(dataBank).getLocally(with(any(Long.class)));
                will(returnValue(new Data()));
            }
        });

        handler.dataBank = dataBank;

        int statusCode = handler.createStatusCode(1000L);

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

    private HttpPutMethodHandler createHandler(
            final Marshaller<Version> versionMarshaller) {
        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        final HttpPutMethodHandler handler = new HttpPutMethodHandler(dataBank,
                hashFunction, new DefaultProtocolDecoder(
                        new MessageMarshallerFactory(versionMarshaller)),
                new JGroupsNode(localId, new IpAddress(7000)));

        return handler;
    }
}
