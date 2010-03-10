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

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class BufferTest {

    private static final int DATA_LENGTH = 250;

    @Test
    public void testWrap() {

        Buffer buffer = Buffer.wrap(new byte[DATA_LENGTH]);

        assertEquals("Wrap result is incorrect", DATA_LENGTH, buffer.size());
    }

    @Test
    public void testAsDataOutputAndInputStream() throws IOException {
        Buffer buffer = Buffer.allocate();

        buffer.asDataOutpuStream().write(new byte[DATA_LENGTH]);

        assertEquals("Output length is incorrect", DATA_LENGTH, buffer.size());

        assertEquals("Input length is incorrect", DATA_LENGTH, buffer.size());
    }

}
