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
package org.hydracache.server.harmony.storage;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.resolver.ResolutionResult;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.harmony.core.Space;

/**
 * Harmony based {@link DataBank} implementation that implements the Quorum like
 * system
 * 
 * @author nzhu
 * 
 */
public class HarmonyDataBank implements DataBank {
    private static Logger log = Logger.getLogger(HarmonyDataBank.class);

    public static final int DEFAULT_W = 2;

    public static final int DEFAULT_R = 1;

    private DataBank localDataBank;

    private Space space;

    private ConflictResolver conflictResolver;

    private int expectedWrites = DEFAULT_W;

    private int expectedReads = DEFAULT_R;

    public HarmonyDataBank(DataBank localDataBank,
            ConflictResolver conflictResolver, Space space) {
        this.localDataBank = localDataBank;
        this.conflictResolver = conflictResolver;
        this.space = space;
    }

    public void setExpectedReads(int expectedReads) {
        this.expectedReads = expectedReads;
    }

    public void setExpectedWrites(int expectedWrites) {
        this.expectedWrites = expectedWrites;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#get(java.lang.Long)
     */
    @Override
    public Data get(Long keyHash) throws IOException {
        GetOperation getOperation = new GetOperation(space.getLocalNode()
                .getId(), keyHash);

        Collection<ResponseMessage> responses = space.broadcast(getOperation);

        ensureReliableGet(responses);

        Data latestData = getLatestData(keyHash, responses);

        return latestData;
    }

    private void ensureReliableGet(Collection<ResponseMessage> responses)
            throws ReliableDataStorageException {
        if (notEnoughGets(responses)) {
            throw new ReliableDataStorageException(
                    "Not enough members participated this reliable GET operation - expected["
                            + expectedReads + "] : received["
                            + responses.size() + "]");
        }
    }

    private boolean notEnoughGets(Collection<ResponseMessage> responses) {
        return responses == null || responses.size() < expectedReads;
    }

    private Data getLatestData(Long keyHash,
            Collection<ResponseMessage> responses) throws IOException {
        Collection<Data> getOperationDataResults = listAllGetResults(keyHash,
                responses);

        return consolidateGetResults(getOperationDataResults);
    }

    private Collection<Data> listAllGetResults(Long keyHash,
            Collection<ResponseMessage> responses) throws IOException {
        Collection<Data> getOperationDataResults = new HashSet<Data>();

        for (ResponseMessage responseMessage : responses) {
            if (!(responseMessage instanceof GetOperationResponse)) {
                log.warn("Unexpected response message received, discarding: "
                        + responseMessage);
                continue;
            }

            GetOperationResponse getResponse = (GetOperationResponse) responseMessage;

            if (getResponse.getResult() != null)
                getOperationDataResults.add(getResponse.getResult());
        }

        if (localDataBank.get(keyHash) != null)
            getOperationDataResults.add(localDataBank.get(keyHash));

        return getOperationDataResults;
    }

    private Data consolidateGetResults(Collection<Data> getOperationDataResults) {
        ResolutionResult resolutionResult = conflictResolver
                .resolve(getOperationDataResults);

        Data latestData = (Data) resolutionResult.getAlive().iterator().next();

        return latestData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#getAll()
     */
    @Override
    public Collection<Data> getAll() {
        return localDataBank.getAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.data.storage.DataBank#put(org.hydracache.server
     * .data.storage.Data)
     */
    @Override
    public void put(Data data) throws IOException {
        PutOperation putOperation = new PutOperation(space.getLocalNode()
                .getId(), data);

        Collection<ResponseMessage> responses = space.broadcast(putOperation);

        ensureReliablePut(responses);

        putLocally(data);
    }

    private void ensureReliablePut(Collection<ResponseMessage> helps)
            throws ReliableDataStorageException {
        if (notEnoughPuts(helps)) {
            throw new ReliableDataStorageException(
                    "Not enough members participated this reliable PUT operation - expected["
                            + expectedWrites + "] : received[" + helps.size()
                            + "]");
        }
    }

    private boolean notEnoughPuts(Collection<ResponseMessage> helps) {
        return helps == null || helps.size() < expectedWrites;
    }

    public void putLocally(Data data) throws IOException {
        localDataBank.put(data);
    }

    public Data getLocally(Long keyHash) throws IOException {
        return localDataBank.get(keyHash);
    }

}
