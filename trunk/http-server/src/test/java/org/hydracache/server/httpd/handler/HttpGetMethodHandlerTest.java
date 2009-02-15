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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.hydracache.io.Buffer;
import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class HttpGetMethodHandlerTest {

    private final Mockery context = new Mockery();

    private IncrementVersionFactory versionFactoryMarshaller;

    private HttpGetMethodHandler handler;

    @Before
    public void initialize() {
        versionFactoryMarshaller = new IncrementVersionFactory();
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());

        handler = createHandler(versionFactoryMarshaller);
    }

    @Test
    public void ensureCorrectDataSerialization() throws IOException {
        final Version version = versionFactoryMarshaller
                .create(new Identity(1));

        final int dataLength = 10;

        final Data data = new Data(1234L, version, new byte[dataLength]);

        final Buffer buffer = handler.serializeDataMessage(data);

        assertNotNull("Buffer is null", buffer);

        assertTrue("Result data length is incorrect",
                buffer.toByteArray().length > dataLength);
    }

    @Test
    public void ensureSerializeNullDataShouldOutputZeroLengthBytes()
            throws IOException {

        final int dataLength = 0;

        final Data data = null;

        final Buffer buffer = handler.serializeDataMessage(data);

        assertNotNull("Buffer is null", buffer);

        assertEquals("Result data length is incorrect", dataLength, buffer
                .toByteArray().length);
    }

    private HttpGetMethodHandler createHandler(
            final Marshaller<Version> versionMarshaller) {

        final DataBank dataBank = context.mock(DataBank.class);

        final HttpGetMethodHandler handler = new HttpGetMethodHandler(dataBank,
                new DefaultProtocolEncoder(new MessageMarshallerFactory(
                        versionMarshaller)));

        return handler;
    }

}
