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
package org.hydracache.io;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class IoUtilsTest {

    /**
     * Test method for
     * {@link org.hydracache.io.IoUtils#readRemainingBytes(java.io.DataInputStream)}
     * .
     * 
     * @throws IOException
     */
    @Test
    public void testReadRemainingBytes() throws IOException {
        byte[] data = new byte[200];

        ByteArrayInputStream in = new ByteArrayInputStream(data);

        byte[] newData = IoUtils.readRemainingBytes(in);

        assertEquals("Incorrect number of bytes read", data.length,
                newData.length);
    }

}
