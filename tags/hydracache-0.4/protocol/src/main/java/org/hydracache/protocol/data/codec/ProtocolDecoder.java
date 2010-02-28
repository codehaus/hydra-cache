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
package org.hydracache.protocol.data.codec;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Protocol decoder interface
 * 
 * @author nzhu
 * 
 */
public interface ProtocolDecoder<T> {

    /**
     * Decode the given input stream to a {@link DataMessage}
     * 
     * @param input
     *            input data
     * @return decoded {@link DataMessage} instance or null if no message
     *         marshaller found
     * @throws IOException
     *             io exception
     */
    T decode(DataInputStream input) throws IOException;

    /**
     * Decode the given input xml string to a {@link DataMessage}
     * 
     * @param input
     *            input xml data
     * @return decoded {@link DataMessage} instance or null if no message
     *         marshaller found
     * @throws IOException
     *             io exception
     */
    T decodeXml(String xml) throws IOException;

}