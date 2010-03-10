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
package org.hydracache.server.data.versioning;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import org.hydracache.server.data.AbstractVersionTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author David Dossot (david@dossot.net)
 */
public abstract class AbstractVersionFactoryTest extends AbstractVersionTest {

    private VersionFactory vf;

    @Before
    public void initializeVersionFactory() throws UnknownHostException {
        vf = newVersionFactory();
    }

    protected abstract VersionFactory newVersionFactory();

    @Test
    public void createIncrement() {
        final Version version = vf.create(A);
        assertThat(version, is(instanceOf(getExpectedClass())));
    }

    protected abstract Class<?> getExpectedClass();

    @Test
    public void expectedWrittenSize() throws IOException {
        final Version a1 = vf.create(A);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        vf.writeObject(a1, dos);
        dos.close();

        assertEquals(getExpectedWrittenSize(), baos.size());
    }

    protected abstract int getExpectedWrittenSize();

    @Test
    public void writeThenReadSingleNode() throws IOException {
        doWriteThenRead(vf.create(A));
    }

    @Test
    public void writeThenReadIncremented() throws IOException {
        final Version v = vf.create(A).incrementFor(B).incrementFor(C);
        doWriteThenRead(v);
    }

    private void doWriteThenRead(final Version version) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(baos);
        vf.writeObject(version, dos);
        dos.close();

        final Version a1bis = vf.readObject(new DataInputStream(
                new ByteArrayInputStream(baos.toByteArray())));

        assertEquals(version, a1bis);
    }
}
