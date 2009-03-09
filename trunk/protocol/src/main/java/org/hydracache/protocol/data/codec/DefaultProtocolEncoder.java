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

import java.io.DataOutputStream;
import java.io.IOException;

import org.hydracache.io.Marshaller;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.BlobDataMessage;

/**
 * Default protocol encoder implementation
 * 
 * @author nzhu
 * 
 */
public class DefaultProtocolEncoder implements ProtocolEncoder<BlobDataMessage> {
    private MessageMarshallerFactory marshallerFactory;

    /**
     * Constructor
     */
    public DefaultProtocolEncoder(MessageMarshallerFactory marshallerFactory) {
        this.marshallerFactory = marshallerFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.protocol.data.codec.ProtocolEncoder#encode(org.hydracache
     * .protocol.data.message.ProtocolMessage, java.io.DataOutput)
     */
    @Override
    public void encode(BlobDataMessage msg, DataOutputStream out)
            throws IOException {
        out.writeShort(HEADER_LENGTH);

        out.writeByte(PROTOCOL_VERSION);

        out.writeShort(msg.getMessageType());

        Marshaller<BlobDataMessage> marshaller = marshallerFactory
                .createMarshallerFor(msg.getMessageType());

        marshaller.writeObject(msg, out);
    }

}
