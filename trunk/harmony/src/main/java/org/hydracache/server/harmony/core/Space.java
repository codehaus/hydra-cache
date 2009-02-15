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
package org.hydracache.server.harmony.core;

import java.io.IOException;
import java.util.Collection;

import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.protocol.control.message.RequestMessage;
import org.hydracache.protocol.control.message.ResponseMessage;

/**
 * Harmony space interface
 * 
 * @author nzhu
 * 
 */
public interface Space {

    /**
     * Get the local server node
     * 
     * @return local server node instance
     */
    Node getLocalNode();

    /**
     * Find all the substances that the local node belongs to
     * 
     * @return a set of all the substances that the local node belongs to
     */
    SubstanceSet findSubstancesForLocalNode();

    /**
     * Get all substances contained in this space
     * 
     * @return a set of all substances contained in this space
     */
    SubstanceSet findAllSubstances();

    /**
     * Broadcast the given help request message to the entire space and return
     * the response
     * 
     * @param request
     *            message to be broadcasted
     * @return all the help response received
     * @throws IOException
     *             thrown if failed to send out the help request
     */
    Collection<ResponseMessage> broadcast(RequestMessage request)
            throws IOException;

    /**
     * Broadcast the given response message to the entire space
     * 
     * @param response
     *            response message
     */
    void broadcast(PutOperationResponse response) throws IOException;

    /**
     * Terminate participation with this space
     */
    void close();

    boolean isActive();

}