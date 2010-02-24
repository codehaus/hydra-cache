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
package org.hydracache.server;

import java.io.IOException;
import java.io.StringReader;
import java.net.Inet4Address;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hydracache.io.XmlMarshaller;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * @author nzhu
 * 
 */
public class IdentityXmlMarshaller implements XmlMarshaller<Identity> {
    private static Logger log = Logger.getLogger(IdentityXmlMarshaller.class);

    public static final String PORT_ATTRIBUTE_NAME = "port";
    public static final String ADDRESS_ATTRIBUTE_NAME = "address";
    public static final String ID_ELEMENT_NAME = "identity";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.XmlMarshaller#writeObject(org.hydracache.server
     * .Identity)
     */
    public Element writeObject(Identity id) throws IOException {
        if (id == null)
            return new Element(ID_ELEMENT_NAME);

        Element idElement = new Element(ID_ELEMENT_NAME);

        idElement.setAttribute(new Attribute(ADDRESS_ATTRIBUTE_NAME, Base64
                .encodeBase64String(id.getAddress().getAddress())));
        idElement.setAttribute(new Attribute(PORT_ATTRIBUTE_NAME, String
                .valueOf(id.getPort())));

        return idElement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.XmlMarshaller#readObject(java.lang.String)
     */
    public Identity readObject(String xml) throws IOException {
        try {
            if (StringUtils.isBlank(xml))
                return Identity.NULL_IDENTITY;

            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(new StringReader(xml));

            Element idElement = doc.getRootElement();

            String addressAttributeValue = idElement
                    .getAttributeValue(ADDRESS_ATTRIBUTE_NAME);
            String portAttributeValue = idElement
                    .getAttributeValue(PORT_ATTRIBUTE_NAME);

            if (StringUtils.isBlank(addressAttributeValue)
                    || StringUtils.isBlank(portAttributeValue))
                return Identity.NULL_IDENTITY;

            byte[] address = Base64.decodeBase64(addressAttributeValue);
            short port = Short.valueOf(portAttributeValue);

            return new Identity(Inet4Address.getByAddress(address), port);
        } catch (Exception jdex) {
            log.error("Failed to parse input xml", jdex);
            return Identity.NULL_IDENTITY;
        }
    }

}
