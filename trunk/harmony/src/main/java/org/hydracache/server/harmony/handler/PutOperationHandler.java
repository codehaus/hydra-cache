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
package org.hydracache.server.harmony.handler;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.harmony.core.Space;

/**
 * Class designed to handle {@link PutOperation} requests
 * 
 * @author nzhu
 * 
 */
public class PutOperationHandler implements ControlMessageHandler {
    private static Logger log = Logger.getLogger(PutOperationHandler.class);

    private DataBank dataBank;

    private Space space;

    public PutOperationHandler(Space space, DataBank dataBank) {
        super();
        this.dataBank = dataBank;
        this.space = space;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.harmony.handler.ControlMessageHandler#handle(org
     * .hydracache.protocol.control.message.ControlMessage)
     */
    public void handle(ControlMessage message) throws Exception {
        Validate.isTrue(message instanceof PutOperation, "Unsupported message["
                + message + "] received");

        PutOperation putOperation = (PutOperation) message;

        if (messageIsNotFromOurNeighbor(putOperation)) {
            log.debug("Discarding message[" + putOperation + "] from stranger");
            return;
        }

        dataBank.put(putOperation.getData());

        PutOperationResponse response = new PutOperationResponse(space
                .getLocalNode().getId(), putOperation.getId());

        space.broadcast(response);

        log.debug("Response message has been sent: " + message);
    }

    private boolean messageIsNotFromOurNeighbor(PutOperation putOperation) {
        return !space.findSubstancesForLocalNode().isNeighbor(
                putOperation.getSource());
    }
}
