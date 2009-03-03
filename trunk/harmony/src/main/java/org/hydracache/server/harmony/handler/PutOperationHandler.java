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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.resolver.ResolutionResult;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Class designed to handle {@link PutOperation} requests
 * 
 * @author nzhu
 * 
 */
public class PutOperationHandler implements ControlMessageHandler {
    private static Logger log = Logger.getLogger(PutOperationHandler.class);

    private HarmonyDataBank harmonyDataBank;

    private ConflictResolver conflictResolver;

    private Space space;

    public PutOperationHandler(Space space, HarmonyDataBank harmonyDataBank,
            ConflictResolver conflictResolver) {
        super();
        this.harmonyDataBank = harmonyDataBank;
        this.space = space;
        this.conflictResolver = conflictResolver;
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

        Data dataToPut = putOperation.getData();
        
        Data currentData = consolidateWithLocalData(dataToPut);

        harmonyDataBank.putLocally(currentData);

        broadcastPutResponse(putOperation);

        log.debug("Response message has been sent: " + message);
    }

    private boolean messageIsNotFromOurNeighbor(PutOperation putOperation) {
        return !space.findSubstancesForLocalNode().isNeighbor(
                putOperation.getSource());
    }

    private Data consolidateWithLocalData(Data dataToPut) {
        Data existingData = harmonyDataBank.getLocally(dataToPut.getKeyHash());
        Data result = dataToPut;
        
        if (existingData != null) {
            Collection<Data> liveData = performConsolidation(dataToPut,
                    existingData);

            result = liveData.iterator().next();
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Collection<Data> performConsolidation(Data dataToPut,
            Data existingData) {
        ResolutionResult result = conflictResolver.resolve(Arrays.asList(
                dataToPut, existingData));

        Validate.isTrue(!result.stillHasConflict(),
                "Unexpected version conflict encountered");

        Collection<Data> liveData = (Collection<Data>) result.getAlive();
        return liveData;
    }

    private void broadcastPutResponse(PutOperation putOperation)
            throws IOException {
        PutOperationResponse response = new PutOperationResponse(space
                .getLocalNode().getId(), putOperation.getId());

        space.broadcast(response);
    }

}
