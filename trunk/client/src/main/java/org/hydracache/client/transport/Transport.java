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
package org.hydracache.client.transport;

/**
 * Represents an abstraction of a message transport. Implementations of this
 * class can be specific to the communication protocol.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public interface Transport {
    /**
     * Establish a connection to the other end for transport.
     * 
     * @param inetAddress
     *            The unique location of the resource
     * @return The transport object for chaining
     */
    Transport establishConnection(String hostName, int port);

    /**
     * Send the request over the wire using the transport. Specific
     * implementations should handle all details of response codes.
     * @param requestMessage TODO
     * @return The response from the request
     * @throws Exception
     */
    ResponseMessage sendRequest(RequestMessage requestMessage) throws Exception;

    /**
     * Clean up the connection for this transport, closing all endpoints.
     */
    void cleanUpConnection();

    void registerHandler(Integer statusCode, ResponseMessageHandler handler);
}
