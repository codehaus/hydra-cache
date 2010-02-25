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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;

import org.hydracache.protocol.data.message.DataMessage;

/**
 * Protocol encoder interface
 * 
 * @author nzhu
 * 
 */
public interface ProtocolEncoder<T> {

    /**
     * Encode the given message to the data output
     * 
     * @param msg
     *            message to be encoded
     * @param out
     *            data output
     * @throws IOException
     *             io exception
     */
    void encode(T msg, DataOutputStream out) throws IOException;

    /**
     * Encode the given message in xml through the given writer
     * 
     * @param dataMsg
     *            message to be encoded
     * @param out
     *            data output
     * @throws IOException
     *             io exception
     */
    void encodeXml(DataMessage dataMsg, Writer out) throws IOException;

}