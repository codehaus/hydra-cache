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
package org.hydracache.client.partition;

import org.apache.log4j.Logger;
import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.partitioning.SubstancePartition;
import org.hydracache.server.Identity;

public class Messager {
    private static Logger log = Logger.getLogger(Messager.class);

    private Transport transport;

    public Messager(Transport transport) {
        super();
        this.transport = transport;
    }

    public ResponseMessage sendMessage(Identity targetNode,
            SubstancePartition nodePartition, RequestMessage requestMessage)
            throws Exception {
        transport.establishConnection(targetNode.getAddress().getHostName(),
                targetNode.getPort());

        ResponseMessage responseMessage = null;

        try {
            responseMessage = transport.sendRequest(requestMessage);
        } catch (Exception e) {
            log.warn("Failed to send message to node[" + targetNode + "]");

            deactivateNode(nodePartition, targetNode);

            throw e;
        } finally {
            transport.cleanUpConnection();
        }

        return responseMessage;
    }

    private void deactivateNode(SubstancePartition nodePartition,
            Identity identity) {
        log.info("Removing inaccessible node[" + identity + "]");
        nodePartition.remove(identity);
    }

}
