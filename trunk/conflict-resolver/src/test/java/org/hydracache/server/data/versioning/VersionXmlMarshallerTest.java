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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.Identity;
import org.hydracache.server.IdentityXmlMarshaller;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class VersionXmlMarshallerTest {
    IncrementVersionFactory incrementVersionFactory = new IncrementVersionFactory();

    IdentityXmlMarshaller identityXmlMarshaller = new IdentityXmlMarshaller();

    Identity nodeId = new Identity(8080);

    @Test
    public void ensureXmlSerialization() throws Exception {
        Version version = incrementVersionFactory.create(nodeId);

        VersionXmlMarshaller marshaller = new VersionXmlMarshaller(
                identityXmlMarshaller);

        String xml = new XMLOutputter().outputString(marshaller
                .writeObject(version));

        assertNotNull("Xml can not be null", xml);
        assertTrue("Incorrect output", xml.startsWith("<version"));
        assertTrue("Incorrect output", xml.contains("<increment"));
        assertTrue("Incorrect output", xml.contains("<value"));
        assertTrue("Incorrect output", xml.contains("<identity"));
        assertTrue("Incorrect output", xml.endsWith("</version>"));
    }

}
