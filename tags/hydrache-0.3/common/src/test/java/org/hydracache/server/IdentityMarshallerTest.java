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
package org.hydracache.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Inet6Address;

import org.hydracache.io.Buffer;
import org.hydracache.io.Marshaller;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class IdentityMarshallerTest {

    @Test
    public void shouldBeAbleToWriteObject() throws Exception {
        IdentityMarshaller marshaller = new IdentityMarshaller();

        Identity identity = new Identity((short) 8080);

        assertTrue(marshaller instanceof Marshaller);

        Buffer buffer = writeIdentity(marshaller, identity);

        assertTrue("Nothing is written in writeObject",
                buffer.toByteArray().length > 0);
    }

    @Test
    public void shouldBeAbleToReadObject() throws Exception {
        IdentityMarshaller marshaller = new IdentityMarshaller();

        Identity identity = new Identity((short) 8080);

        assertTrue(marshaller instanceof Marshaller);

        Buffer buffer = writeIdentity(marshaller, identity);

        Identity newIdentity = readIdentity(marshaller, buffer);

        assertEquals("readIdentity produced incorrect result", identity,
                newIdentity);
    }

    @Test
    public void shouldBeAbleToHanldIpv6() throws Exception {
        IdentityMarshaller marshaller = new IdentityMarshaller();

        Identity identity = new Identity(Inet6Address.getLocalHost(), 81);

        Buffer buffer = writeIdentity(marshaller, identity);

        Identity newIdentity = readIdentity(marshaller, buffer);

        assertEquals("readIdentity produced incorrect result", identity,
                newIdentity);
    }

    private Buffer writeIdentity(IdentityMarshaller marshaller,
            Identity identity) throws IOException {
        Buffer buffer = Buffer.allocate();

        marshaller.writeObject(identity, buffer.asDataOutpuStream());
        return buffer;
    }

    private Identity readIdentity(IdentityMarshaller marshaller, Buffer buffer)
            throws IOException {
        Identity newIdentity = marshaller
                .readObject(buffer.asDataInputStream());
        return newIdentity;
    }
}
