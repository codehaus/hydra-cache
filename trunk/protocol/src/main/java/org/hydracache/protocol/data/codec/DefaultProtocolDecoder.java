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

import org.hydracache.io.BinaryMarshaller;
import org.hydracache.io.XmlMarshaller;
import org.hydracache.protocol.data.ProtocolException;
import org.hydracache.protocol.data.message.DataMessage;

/**
 * Hydra protocol decoder
 * 
 * @author nzhu
 * 
 */
public class DefaultProtocolDecoder implements ProtocolDecoder<DataMessage> {
    private BinaryMarshaller<DataMessage> binaryMarshaller;

    private XmlMarshaller<DataMessage> xmlMarshaller;

    public DefaultProtocolDecoder(BinaryMarshaller<DataMessage> binaryMarshaller,
            XmlMarshaller<DataMessage> xmlMarshaller) {
        this.binaryMarshaller = binaryMarshaller;
        this.xmlMarshaller = xmlMarshaller;
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

        return binaryMarshaller.readObject(input);
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.protocol.data.codec.ProtocolDecoder#decodeXml(java.lang
     * .String)
     */
    @Override
    public DataMessage decodeXml(String xml) throws IOException {
        return xmlMarshaller.readObject(xml);
    }

}
