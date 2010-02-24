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

import org.hydracache.server.Identity;
import org.hydracache.server.IdentityXmlMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionFactory;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author nzhu
 * 
 */
public class DataMessageXmlMarshallerTest {
    Long keyHash = 100L;

    VersionFactory versionFactory = new IncrementVersionFactory();

    Version version = versionFactory.create(new Identity(8080));

    DataMessageXmlMarshaller marshaller = new DataMessageXmlMarshaller(
            new VersionXmlMarshaller(new IdentityXmlMarshaller(),
                    versionFactory));

    @Test
    public void ensureXmlEncode() throws Exception {
        DataMessage dataMsg = new DataMessage(new Data(keyHash, version,
                "Test Data".getBytes()));

        String xml = new XMLOutputter().outputString(marshaller
                .writeObject(dataMsg));

        assertNotNull("Output is null", xml);
        assertTrue("Missing child element", xml.contains("<data"));
        assertTrue("Missing child element", xml.contains("<version"));
    }

    @Test
    public void ensureXmlDecode() throws Exception {
        DataMessage dataMsg = new DataMessage(new Data(keyHash, version,
                "Test Data".getBytes()));

        String xml = new XMLOutputter().outputString(marshaller
                .writeObject(dataMsg));

        DataMessage restoredMsg = marshaller.readObject(xml);

        assertEquals("Decoded message object is incorrect", dataMsg, restoredMsg);
    }

}
