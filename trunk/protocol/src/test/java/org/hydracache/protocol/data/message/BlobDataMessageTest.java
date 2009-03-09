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
package org.hydracache.protocol.data.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.junit.Test;

public class BlobDataMessageTest {

    private static final int EXPECTED_BYTES = 25;

    @Test
    public void testMessageCanBeWriteToAndReadFromByteBuffer() throws Exception {
        final BlobDataMessage msg = new BlobDataMessage();

        assertTrue(BlobDataMessage.BLOB_DATA_MESSAGE_TYPE == msg.getMessageType());

        final IncrementVersionFactory versionFactoryMarshaller =
                new IncrementVersionFactory();

        versionFactoryMarshaller.setIdentityMarshaller(new IdentityMarshaller());

        final Version version =
                versionFactoryMarshaller.create(new Identity(1));

        msg.setVersion(version);

        msg.setBlob(new byte[10]);

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        final DataMessageMarshaller marshaller =
                new DataMessageMarshaller(versionFactoryMarshaller);

        marshaller.writeObject(msg, new DataOutputStream(buffer));

        assertEquals("Payload length is incorrect", EXPECTED_BYTES,
                buffer.toByteArray().length);

        final BlobDataMessage newMsg =
                marshaller.readObject(new DataInputStream(
                        new ByteArrayInputStream(buffer.toByteArray())));

        assertEquals("Data is deserialized incorrectly", msg,
                newMsg);
    }

}
