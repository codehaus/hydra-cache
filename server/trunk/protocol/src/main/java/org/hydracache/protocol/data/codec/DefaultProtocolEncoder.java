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
import java.io.Writer;

import org.hydracache.io.BinaryMarshaller;
import org.hydracache.io.XmlMarshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Default protocol encoder implementation
 * 
 * @author nzhu
 * 
 */
public class DefaultProtocolEncoder implements ProtocolEncoder<DataMessage> {
    private BinaryMarshaller<DataMessage> binaryMarshaller;
    private XmlMarshaller<DataMessage> xmlMarshaller;

    public DefaultProtocolEncoder(BinaryMarshaller<DataMessage> marshaller,
            XmlMarshaller<DataMessage> xmlMarshaller) {
        this.binaryMarshaller = marshaller;
        this.xmlMarshaller = xmlMarshaller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.protocol.data.codec.ProtocolEncoder#encode(org.hydracache
     * .protocol.data.message.ProtocolMessage, java.io.DataOutput)
     */
    @Override
    public void encode(DataMessage msg, DataOutputStream out)
            throws IOException {
        out.writeShort(HEADER_LENGTH);

        out.writeByte(PROTOCOL_VERSION);

        out.writeShort(msg.getMessageType());

        binaryMarshaller.writeObject(msg, out);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.protocol.data.codec.ProtocolEncoder#encodeXml(org.hydracache
     * .protocol.data.message.DataMessage, java.io.Writer)
     */
    @Override
    public void encodeXml(DataMessage dataMsg, Writer out) throws IOException {
        Element element = xmlMarshaller.writeObject(dataMsg);

        XMLOutputter outputter = new XMLOutputter();
        
        outputter.output(new Document(element), out);
    }

}
