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
package org.hydracache.protocol.data.codec;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.ProtocolException;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class ProtocolCodecTest {

    private static final byte[] TEST_DATA = "Data".getBytes();

    private static final int INVALID_PROTOCOL_VERSION = 9;

    private final IncrementVersionFactory versionFactory = new IncrementVersionFactory();

    private Identity testNodeId;

    @Before
    public void initialize() throws UnknownHostException {
        versionFactory.setIdentityMarshaller(new IdentityMarshaller());
        testNodeId = new Identity(1);
    }

    @Test
    public void testMsgCanBeEncodeAndDecodeProperly() throws Exception {
        final ProtocolEncoder<DataMessage> encoder = buildEncoder();

        final Buffer buffer = Buffer.allocate();

        final DataMessage msg = new DataMessage();

        msg.setVersion(versionFactory.create(testNodeId));
        msg.setBlob(TEST_DATA);

        encoder.encode(msg, buffer.asDataOutpuStream());

        int expectedBinaryLength = 24;

        assertEquals("Ecoding generated invalid length of data",
                expectedBinaryLength, buffer.toByteArray().length);

        final ProtocolDecoder<DataMessage> decoder = buildDecoder();

        final DataMessage newMsg = (DataMessage) decoder.decode(buffer
                .asDataInputStream());

        assertEquals("Decode result is incorrect", msg, newMsg);
    }

    @Test(expected = ProtocolException.class)
    public void testDecodeInvalidProtocolVersion() throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        final DataOutput out = new DataOutputStream(buffer);

        out.writeShort(ProtocolConstants.HEADER_LENGTH);

        out.writeByte(INVALID_PROTOCOL_VERSION);

        final ProtocolDecoder<DataMessage> decoder = buildDecoder();

        decoder.decode(new DataInputStream(new ByteArrayInputStream(buffer
                .toByteArray())));
    }

    @Test(expected = ProtocolException.class)
    public void testDecodeWithShortHeader() throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        final DataOutput out = new DataOutputStream(buffer);

        out.writeShort((short) 2);

        final ProtocolDecoder<DataMessage> decoder = buildDecoder();

        decoder.decode(new DataInputStream(new ByteArrayInputStream(buffer
                .toByteArray())));
    }

    @Test
    public void testDecodeWithLongHeader() throws IOException {
        final Buffer buffer = Buffer.allocate();

        final int headerInjectionLenght = 10;

        buffer.asDataOutpuStream().writeShort(
                ProtocolConstants.HEADER_LENGTH + headerInjectionLenght);

        buffer.asDataOutpuStream()
                .writeByte(ProtocolConstants.PROTOCOL_VERSION);

        final DataMessage msg = new DataMessage();

        msg.setVersion(versionFactory.create(testNodeId));
        msg.setBlob(TEST_DATA);

        buffer.asDataOutpuStream().writeShort(msg.getMessageType());

        buffer.asDataOutpuStream().write(new byte[headerInjectionLenght]);

        versionFactory
                .writeObject(msg.getVersion(), buffer.asDataOutpuStream());
        buffer.asDataOutpuStream().write(msg.getBlob());

        final ProtocolDecoder<DataMessage> decoder = buildDecoder();

        final DataMessage newMsg = (DataMessage) decoder.decode(buffer
                .asDataInputStream());

        assertEquals("Decode result is incorrect", msg, newMsg);
    }

    private ProtocolEncoder<DataMessage> buildEncoder() {

        final ProtocolEncoder<DataMessage> encoder = new DefaultProtocolEncoder(
                new DataMessageMarshaller(versionFactory));

        return encoder;
    }

    private ProtocolDecoder<DataMessage> buildDecoder() {
        final ProtocolDecoder<DataMessage> decoder = new DefaultProtocolDecoder(
                new DataMessageMarshaller(versionFactory));
        return decoder;
    }

}
