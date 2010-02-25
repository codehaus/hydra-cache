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
package org.hydracache.protocol.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

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
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class ProtocolUtilsTest {

    private IncrementVersionFactory versionMarshaller;

    private VersionXmlMarshaller versionXmlMarshaller;

    private DefaultProtocolEncoder defaultProtocolEncoder;

    private DefaultProtocolDecoder defaultProtocolDecoder;

    @Before
    public void initialize() {
        versionMarshaller = new IncrementVersionFactory(
                new IdentityMarshaller());
        versionXmlMarshaller = new VersionXmlMarshaller(
                new IdentityXmlMarshaller(), versionMarshaller);

        DataMessageMarshaller binaryDataMsgMarshaller = new DataMessageMarshaller(
                versionMarshaller);
        DataMessageXmlMarshaller xmlDataMsgMarshaller = new DataMessageXmlMarshaller(
                versionXmlMarshaller);

        defaultProtocolEncoder = new DefaultProtocolEncoder(
                binaryDataMsgMarshaller, xmlDataMsgMarshaller);

        defaultProtocolDecoder = new DefaultProtocolDecoder(
                binaryDataMsgMarshaller, xmlDataMsgMarshaller);
    }

    @Test
    public void ensureCorrectDataSerialization() throws IOException {
        final Version version = versionMarshaller.create(new Identity(1));

        final int dataLength = 10;

        final Data data = new Data(1234L, version, new byte[dataLength]);

        final Buffer buffer = ProtocolUtils.encodeDataMessage(
                defaultProtocolEncoder, data);

        assertNotNull("Buffer is null", buffer);

        assertTrue("Result data length is incorrect",
                buffer.toByteArray().length > dataLength);
    }

    @Test
    public void ensureSerializeNullDataShouldOutputZeroLengthBytes()
            throws IOException {

        final int dataLength = 0;

        final Data data = null;

        final Buffer buffer = ProtocolUtils.encodeDataMessage(
                defaultProtocolEncoder, data);

        assertNotNull("Buffer is null", buffer);

        assertEquals("Result data length is incorrect", dataLength, buffer
                .toByteArray().length);
    }

    @Test
    public void ensureCorrectDecodeDataMessage() throws IOException {
        final DataMessage expectedMessage = createExpectedDataMsg();

        final Buffer buffer = encodeMessageToBuffer(expectedMessage);

        final DataMessage decodedMessage = ProtocolUtils.decodeProtocolMessage(
                defaultProtocolDecoder, buffer.toByteArray());

        assertEquals("Decoded message is incorrect", expectedMessage,
                decodedMessage);
    }

    private Buffer encodeMessageToBuffer(final DataMessage expectedMessage)
            throws IOException {
        final Buffer buffer = Buffer.allocate();

        defaultProtocolEncoder.encode(expectedMessage, buffer
                .asDataOutpuStream());

        return buffer;
    }

    private DataMessage createExpectedDataMsg() throws UnknownHostException {
        final DataMessage expectedMessage = new DataMessage();

        expectedMessage.setBlob(new byte[250]);
        expectedMessage.setVersion(versionMarshaller.create(new Identity(1)));

        return expectedMessage;
    }

}
