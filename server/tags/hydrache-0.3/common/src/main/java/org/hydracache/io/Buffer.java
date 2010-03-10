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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Binary buffer that auto resizes plus some helper methods to make dealing with
 * binary input and output a bit easier
 * 
 * @author nzhu
 * 
 */
public class Buffer {
    private static Logger log = Logger.getLogger(Buffer.class);

    public static final int DEFAULT_BUFFER_SIZE = 64;

    private ByteArrayOutputStream stream;

    /**
     * Allocate a new buffer with default size {@link DEFAULT_BUFFER_SIZE}
     * 
     * @return a new buffer instance
     */
    public static Buffer allocate() {
        return new Buffer(DEFAULT_BUFFER_SIZE);
    }

    /**
     * Allocate a new buffer with the given size
     * 
     * @param size
     * @return a newly created buffer instance with initial size set to the
     *         given size
     */
    public static Buffer allocate(int size) {
        return new Buffer(size);
    }

    private Buffer(int size) {
        stream = new ByteArrayOutputStream(size);
    }

    private Buffer(byte[] data) {
        this(DEFAULT_BUFFER_SIZE);

        try {
            stream.write(data);
        } catch (IOException ex) {
            // should not happen unless we are running stream of heap memory
            log.fatal("Failed to allocate IO buffer: ", ex);
        }
    }

    /**
     * Morph this buffer to a {@link DataOutputStream}
     * 
     * @return data output stream
     */
    public DataOutputStream asDataOutpuStream() {
        return new DataOutputStream(stream);
    }

    /**
     * To byte array
     * 
     * @return content
     */
    public byte[] toByteArray() {
        return stream.toByteArray();
    }

    /**
     * Wrap the given byte array in the form of {@link Buffer}
     * 
     * @param data
     *            binary data
     * @return buffer instance that contains the given data
     */
    public static Buffer wrap(byte[] data) {
        return new Buffer(data);
    }

    /**
     * Return the binary data content as a {@link DataInputStream}
     * 
     * @return a instance of {@link DataInputStream} that contains the binary
     *         content in this buffer
     */
    public DataInputStream asDataInputStream() {
        return new DataInputStream(new ByteArrayInputStream(toByteArray()));
    }

    /**
     * Get the size of this buffer instance
     * 
     * @return buffer size
     */
    public int size() {
        return stream.size();
    }

}
