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
package org.hydracache.protocol.data.mashaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.junit.Before;
import org.junit.Test;

public class DataMessageMarshallerTest {

    private static final int TEST_DATA_SIZE = 10;

    final IncrementVersionFactory versionFactoryMarshaller = new IncrementVersionFactory();

    @Before
    public void setup() {
        versionFactoryMarshaller
                .setIdentityMarshaller(new IdentityMarshaller());
    }

    @Test
    public void ensureNullVersionCanBeWritten() throws Exception {
        final DataMessage msg = new DataMessage();

        msg.setVersion(versionFactoryMarshaller.createNull());

        msg.setBlob(new byte[TEST_DATA_SIZE]);

        final Buffer buffer = Buffer.allocate();

        final DataMessageMarshaller marshaller = new DataMessageMarshaller(
                versionFactoryMarshaller);

        marshaller.writeObject(msg, buffer.asDataOutpuStream());

        assertEquals("Payload length is incorrect", 25, buffer.size());

        final DataMessage newMsg = marshaller.readObject(buffer
                .asDataInputStream());

        assertEquals("Data is deserialized incorrectly", msg, newMsg);
    }

    @Test
    public void ensureMessageCanBeWriteToAndReadFromByteBuffer()
            throws Exception {
        final DataMessage msg = new DataMessage();

        assertTrue(DataMessage.BLOB_DATA_MESSAGE_TYPE == msg.getMessageType());

        final Version version = versionFactoryMarshaller
                .create(new Identity(1));

        msg.setVersion(version);

        msg.setBlob(new byte[10]);

        final Buffer buffer = Buffer.allocate();

        final DataMessageMarshaller marshaller = new DataMessageMarshaller(
                versionFactoryMarshaller);

        marshaller.writeObject(msg, buffer.asDataOutpuStream());

        int expectedBytes = 25;

        assertEquals("Payload length is incorrect", expectedBytes, buffer
                .size());

        final DataMessage newMsg = marshaller.readObject(buffer
                .asDataInputStream());

        assertEquals("Data is deserialized incorrectly", msg, newMsg);
    }

}
