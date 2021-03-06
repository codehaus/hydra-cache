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

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;
import org.hydracache.client.InternalHydraException;
import org.hydracache.client.transport.ConflictStatusHandler;
import org.hydracache.client.transport.DefaultResponseMessageHandler;
import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.partitioning.SubstancePartition;
import org.hydracache.server.Identity;
import org.hydracache.server.data.versioning.VersionConflictException;

public class Messenger {
    private static Logger log = Logger.getLogger(Messenger.class);

    private Transport transport;

    public Messenger(Transport transport) {
        this.transport = transport;

        registerDefaultHandlers();
    }

    private void registerDefaultHandlers() {
        transport.registerHandler(HttpStatus.SC_CONFLICT,
                new ConflictStatusHandler());
        transport.registerHandler(HttpStatus.SC_OK,
                new DefaultResponseMessageHandler());
        transport.registerHandler(HttpStatus.SC_CREATED,
                new DefaultResponseMessageHandler());
    }

    public ResponseMessage sendMessage(Identity target,
            SubstancePartition nodePartition, RequestMessage requestMessage)
            throws Exception {

        Identity currentTarget = target;

        try {
            log.debug("Sending message [" + requestMessage
                    + "] on first attempt to [" + currentTarget + "]");

            ResponseMessage responseMsg = send(nodePartition, requestMessage,
                    currentTarget);

            return responseMsg;
        } catch (VersionConflictException vce) {
            throw vce;
        } catch (Exception ex) {
            return retrySend(target, nodePartition, requestMessage, ex);
        }
    }

    private ResponseMessage send(SubstancePartition nodePartition,
            RequestMessage requestMessage, Identity currentTarget)
            throws Exception {
        try {
            transport.establishConnection(currentTarget.getAddress()
                    .getHostName(), currentTarget.getPort());

            return transport.sendRequest(requestMessage);
        } catch (VersionConflictException vce) {
            throw vce;
        } catch (InternalHydraException ihe) {
            throw ihe;
        } catch (Exception ex) {
            log.warn("Failed to send message to node[" + currentTarget + "]");
            deactivateNode(nodePartition, currentTarget);
            throw ex;
        } finally {
            transport.cleanUpConnection();
        }
    }

    private void deactivateNode(SubstancePartition nodePartition,
            Identity identity) {
        log.info("Removing inaccessible node[" + identity + "]");
        nodePartition.remove(identity);
    }

    private ResponseMessage retrySend(Identity target,
            SubstancePartition nodePartition, RequestMessage requestMessage,
            Exception rootCause) throws Exception {
        Identity currentTarget;

        currentTarget = nodePartition.next(target);

        if (currentTarget == null) {
            log.warn("Empty space detected, no more node left in the space for retry");
            throw rootCause;
        }

        log.debug("Communication failed with the current target[" + target
                + "] retrying next target[" + currentTarget + "]");

        ResponseMessage responseMsg = send(nodePartition, requestMessage,
                currentTarget);

        return responseMsg;
    }

}
