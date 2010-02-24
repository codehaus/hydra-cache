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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
    private static Logger log = Logger.getLogger(VersionXmlMarshaller.class);

    private static final String VALUE_ELEMENT_NAME = "value";

    private static final String INCREMENT_ELEMENT_NAME = "increment";

    private static final String VERSION_ELEMENT_NAME = "version";

    private IdentityXmlMarshaller identityXmlMarshaller;

    private VersionFactory versionFactory;

    public VersionXmlMarshaller(IdentityXmlMarshaller identityXmlMarshaller,
            VersionFactory versionFactory) {
        this.identityXmlMarshaller = identityXmlMarshaller;
        this.versionFactory = versionFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.XmlMarshaller#readObject(java.lang.String)
     */
    @Override
    public Version readObject(String xml) throws IOException {
        if (StringUtils.isBlank(xml))
            return versionFactory.createNull();

        SAXBuilder builder = new SAXBuilder();

        try {
            Document doc = builder.build(new StringReader(xml));
            Element versionElement = doc.getRootElement();

            Element incrementElement = versionElement
                    .getChild(INCREMENT_ELEMENT_NAME);

            Element nodeIdElement = incrementElement
                    .getChild(IdentityXmlMarshaller.ID_ELEMENT_NAME);

            Identity nodeId = identityXmlMarshaller
                    .readObject(new XMLOutputter().outputString(nodeIdElement));

            long value = Long.valueOf(incrementElement.getChild(
                    VALUE_ELEMENT_NAME).getValue());

            return new Increment(nodeId, value);
        } catch (JDOMException e) {
            log.error("Failed to parse input xml", e);
            return versionFactory.createNull();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.io.XmlMarshaller#writeObject(java.lang.Object)
     */
    @Override
    public Element writeObject(Version id) throws IOException {
        if (id == null)
            return new Element(VERSION_ELEMENT_NAME);

        Increment increment = (Increment) id;

        Element versionElement = new Element(VERSION_ELEMENT_NAME);

        Element incrementElement = new Element(INCREMENT_ELEMENT_NAME);

        incrementElement.addContent(identityXmlMarshaller.writeObject(increment
                .getNodeId()));

        Element valueElement = new Element(VALUE_ELEMENT_NAME)
                .addContent(String.valueOf(increment.getValue()));

        incrementElement.addContent(valueElement);

        versionElement.addContent(incrementElement);

        return versionElement;
    }
}
