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

import static org.hydracache.protocol.data.codec.ProtocolConstants.HEADER_LENGTH;
import static org.hydracache.protocol.data.codec.ProtocolConstants.PROTOCOL_VERSION;

import java.io.DataInputStream;
import java.io.IOException;

import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.ProtocolException;
import org.hydracache.protocol.data.message.DataMessage;

/**
 * Hydra protocol decoder
 * 
 * @author nzhu
 * 
 */
public class BinaryProtocolDecoder implements ProtocolDecoder<DataMessage> {
    private Marshaller<DataMessage> marshaller;

    public BinaryProtocolDecoder(Marshaller<DataMessage> marshaller) {
        this.marshaller = marshaller;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.hydracache.protocol.data.codec.ProtocolDecoder#decode(java.io.
     * DataInputStream)
     */
    @Override
    public DataMessage decode(DataInputStream input) throws IOException {
        decodeHeader(input);

        return marshaller.readObject(input);
    }

    private short decodeHeader(DataInputStream input) throws IOException,
            ProtocolException {
        short headerLength = input.readShort();

        validateHeaderLength(headerLength);

        byte protocolVersion = input.readByte();

        validateProtocolVersion(protocolVersion);

        short messageType = input.readShort();

        if (headerLength > HEADER_LENGTH) {
            skipUnknownHeader(input, headerLength);
        }

        return messageType;
    }

    private void skipUnknownHeader(DataInputStream input, short headerLength)
            throws IOException {
        int delta = headerLength - HEADER_LENGTH;
        input.skip(delta);
    }

    private void validateProtocolVersion(byte protocolVersion)
            throws ProtocolException {
        if (PROTOCOL_VERSION != protocolVersion)
            throw new ProtocolException("Protocol version [" + protocolVersion
                    + "] is not supported");
    }

    private void validateHeaderLength(short headerLength)
            throws ProtocolException {
        if (headerLength < HEADER_LENGTH)
            throw new ProtocolException("Invalid protocol header length ["
                    + headerLength + "]");
    }

}
