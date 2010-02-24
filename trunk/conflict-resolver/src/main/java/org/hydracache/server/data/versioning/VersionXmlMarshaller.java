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
package org.hydracache.server.data.versioning;

import java.io.IOException;
import java.io.StringReader;

import org.hydracache.io.XmlMarshaller;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityXmlMarshaller;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author nzhu
 * 
 */
public class VersionXmlMarshaller implements XmlMarshaller<Version> {

    private IdentityXmlMarshaller identityXmlMarshaller;

    public VersionXmlMarshaller(IdentityXmlMarshaller identityXmlMarshaller) {
        this.identityXmlMarshaller = identityXmlMarshaller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.XmlMarshaller#readObject(java.lang.String)
     */
    @Override
    public Version readObject(String xml) throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new StringReader(xml));
            Element versionElement = doc.getRootElement();

            Element incrementElement = versionElement.getChild("increment");

            Element nodeIdElement = incrementElement
                    .getChild(IdentityXmlMarshaller.ID_ELEMENT_NAME);

            Identity nodeId = identityXmlMarshaller
                    .readObject(new XMLOutputter().outputString(nodeIdElement));

            long value = Long.valueOf(incrementElement.getChild("value")
                    .getValue());

            return new Increment(nodeId, value);
        } catch (JDOMException e) {
            throw new IOException("Failed to parse input xml", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.XmlMarshaller#writeObject(java.lang.Object)
     */
    @Override
    public Element writeObject(Version id) throws IOException {
        Increment increment = (Increment) id;

        Element versionElement = new Element("version");

        Element incrementElement = new Element("increment");

        incrementElement.addContent(identityXmlMarshaller.writeObject(increment
                .getNodeId()));

        Element valueElement = new Element("value").addContent(String
                .valueOf(increment.getValue()));

        incrementElement.addContent(valueElement);

        versionElement.addContent(incrementElement);

        return versionElement;
    }
}
