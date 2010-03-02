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

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author nzhu
 * 
 */
public class IdentityXmlMarshallerTest {
    IdentityXmlMarshaller marshaller = new IdentityXmlMarshaller();

    Identity id = new Identity(8080);

    @Test
    public void ensureIdentityCanBeEncodedToXml() throws IOException {
        Element e = marshaller.writeObject(id);

        String xml = new XMLOutputter().outputString(e);

        assertTrue("xml output is incorrect", xml.startsWith("<identity"));
        assertTrue("xml output is incorrect", xml.contains("<address>"));
        assertTrue("xml output is incorrect", xml.contains("<port>8080</port>"));
        assertTrue("Missing cdata element", xml.contains("CDATA"));        
    }

    @Test
    public void ensureIdentityCanBeDecodedFromXml() throws IOException {
        Element e = marshaller.writeObject(id);

        String xml = new XMLOutputter().outputString(e);

        Identity newId = marshaller.readObject(xml);

        assertEquals("Id is incorrect", id, newId);
    }

    @Test
    public void ensureIdentityCanEncodeNull() throws IOException {
        Element e = marshaller.writeObject(null);

        String xml = new XMLOutputter().outputString(e);

        assertEquals("Incorrect output", "<identity />", xml);
    }

    @Test
    public void ensureIdentityCanDecodeNull() throws IOException {
        Identity newId = marshaller.readObject(null);

        assertEquals("Incorrect id", Identity.NULL_IDENTITY, newId);
    }

    @Test
    public void ensureIdentityCanDecodeBlank() throws IOException {
        Identity newId = marshaller.readObject(" ");

        assertEquals("Incorrect id", Identity.NULL_IDENTITY, newId);
    }

    @Test
    public void ensureIdentityCanDecodeBlankXml() throws IOException {
        Identity newId = marshaller.readObject(" <identity /> ");

        assertEquals("Incorrect id", Identity.NULL_IDENTITY, newId);
    }

    @Test
    public void ensureIdentityCanDecodeInvalidXml() throws IOException {
        Identity newId = marshaller.readObject(" < identity > ");

        assertEquals("Incorrect id", Identity.NULL_IDENTITY, newId);
    }

}
