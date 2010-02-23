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
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * @author nzhu
 * 
 */
public class IdentityXmlMarshaller {

    private static final String PORT_ATTRIBUTE_NAME = "port";
    private static final String ADDRESS_ATTRIBUTE_NAME = "address";
    private static final String ID_ELEMENT_NAME = "identity";

    public String writeObject(Identity id) throws IOException {
        Element idElement = new Element(ID_ELEMENT_NAME);

        idElement.setAttribute(new Attribute(ADDRESS_ATTRIBUTE_NAME, Base64
                .encodeBase64String(id.getAddress().getAddress())));
        idElement.setAttribute(new Attribute(PORT_ATTRIBUTE_NAME, String
                .valueOf(id.getPort())));

        XMLOutputter outputer = new XMLOutputter();

        return outputer.outputString(idElement);
    }

    public Identity readObject(String xml) throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();

            Document doc = builder.build(new StringReader(xml));

            Element idElement = doc.getRootElement();

            byte[] address = Base64.decodeBase64(idElement
                    .getAttributeValue(ADDRESS_ATTRIBUTE_NAME));
            short port = Short.valueOf(idElement
                    .getAttributeValue(PORT_ATTRIBUTE_NAME));

            return new Identity(Inet4Address.getByAddress(address), port);
        } catch (JDOMException jdex) {
            throw new IOException("Failed to read object", jdex);
        }
    }

}
