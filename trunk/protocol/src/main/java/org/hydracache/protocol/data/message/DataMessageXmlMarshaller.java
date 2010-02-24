/*
 * Copyright 2010 the original author or authors.
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

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.codec.binary.Base64;
import org.hydracache.io.XmlMarshaller;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author nzhu
 * 
 */
public class DataMessageXmlMarshaller implements XmlMarshaller<DataMessage> {

    private VersionXmlMarshaller versionXmlMarshaller;

    public DataMessageXmlMarshaller(VersionXmlMarshaller versionXmlMarshaller) {
        this.versionXmlMarshaller = versionXmlMarshaller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.XmlMarshaller#readObject(java.lang.String)
     */
    @Override
    public DataMessage readObject(String xml) throws IOException {
        SAXBuilder builder = new SAXBuilder();

        try {
            Document doc = builder.build(new StringReader(xml));
            Element messageElement = doc.getRootElement();
            
            Element dataElement = messageElement.getChild("data");
            byte[] data = Base64.decodeBase64(dataElement.getValue());
            
            Element versionElement = messageElement.getChild(VersionXmlMarshaller.VERSION_ELEMENT_NAME);
            Version version = versionXmlMarshaller.readObject(new XMLOutputter().outputString(versionElement));            
            
            return new DataMessage(version, data);
        } catch (Exception ex) {
            throw new IOException("Failed to read from xml", ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.XmlMarshaller#writeObject(java.lang.Object)
     */
    @Override
    public Element writeObject(DataMessage id) throws IOException {
        Element messageElement = new Element("message");

        messageElement.addContent(new Element("data").addContent(Base64
                .encodeBase64String(id.getBlob())));

        Element versionElement = versionXmlMarshaller.writeObject(id
                .getVersion());

        messageElement.addContent(versionElement);

        return messageElement;
    }
}
