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
import java.util.Collections;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.DeleteOperation;
import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.protocol.control.message.VersionConflictRejection;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.resolver.ResolutionResult;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.versioning.VersionConflictException;
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
     * @see org.hydracache.server.data.storage.DataBank#get(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public Data get(String context, Long keyHash) throws IOException {
        int expectedReads = this.expectedReads;

        if (hasLocalCopy(context, keyHash))
            expectedReads--;

        Collection<ResponseMessage> responses = Collections.emptyList();

        if (requireReliableGet(expectedReads)) {
            GetOperation getOperation = new GetOperation(space.getLocalNode()
                    .getId(), context, keyHash);

            responses = space.broadcast(getOperation);
        }

        if (dataNotFound(context, keyHash, responses))
            return null;

        ensureReliableGet(responses, expectedReads);

        Data latestData = getLatestData(context, keyHash, responses);

        putLocally(context, latestData);

        return latestData;
    }

    private boolean hasLocalCopy(String context, Long keyHash)
            throws IOException {
        return getLocally(context, keyHash) != null;
    }

    private boolean requireReliableGet(int expectedReads) {
        return expectedReads > 0;
    }

    private boolean dataNotFound(String context, Long keyHash,
            Collection<ResponseMessage> responses) throws IOException {
        Data localData = localDataBank.get(context, keyHash);
        return responses.isEmpty() && localData == null;
    }

    private void ensureReliableGet(Collection<ResponseMessage> responses,
            int expectedReads) throws ReliableDataStorageException {
        if (notEnoughGets(responses, expectedReads)) {
            throw new ReliableDataStorageException(
                    "Not enough members participated this reliable GET operation - expected["
                            + expectedReads + "] : received["
                            + responses.size() + "]");
        }
    }

    private boolean notEnoughGets(Collection<ResponseMessage> responses,
            int expectedReads) {
        return responses == null || responses.size() < expectedReads;
    }

    private Data getLatestData(String context, Long keyHash,
            Collection<ResponseMessage> responses) throws IOException {
        Collection<Data> getOperationDataResults = listAllGetResults(context,
                keyHash, responses);

        return consolidateGetResults(getOperationDataResults);
    }

    private Collection<Data> listAllGetResults(String context, Long keyHash,
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

        if (localDataBank.get(context, keyHash) != null)
            getOperationDataResults.add(localDataBank.get(context, keyHash));

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
    public Collection<Data> getAll() throws IOException {
        return localDataBank.getAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#delete(java.lang.String,
     * java.lang.Long)
     */
    @Override
    public void delete(String context, Long keyHash) throws IOException {
        if (isReliableWriteRequired()) {
            DeleteOperation deleteOperation = new DeleteOperation(space
                    .getLocalNode().getId(), context, keyHash);

            Collection<ResponseMessage> responses = space
                    .broadcast(deleteOperation);

            ensureReliableDelete(responses);
        }

        localDataBank.delete(context, keyHash);
    }

    private void ensureReliableDelete(Collection<ResponseMessage> responses)
            throws ReliableDataStorageException {
        if (notEnoughWrites(responses)) {
            throw new ReliableDataStorageException(
                    "Not enough members participated this reliable DELETE operation - expected["
                            + expectedWrites + "] : received["
                            + responses.size() + "]");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.data.storage.DataBank#put(java.lang.String,
     * org.hydracache.server.data.storage.Data)
     */
    @Override
    public void put(String context, Data data) throws IOException,
            VersionConflictException {
        if (isReliableWriteRequired()) {
            PutOperation putOperation = new PutOperation(space.getLocalNode()
                    .getId(), context, data);

            Collection<ResponseMessage> responses = space
                    .broadcast(putOperation);

            ensureReliablePut(responses);
        }

        putLocally(context, data);
    }

    private boolean isReliableWriteRequired() {
        return expectedWrites > 1;
    }

    private void ensureReliablePut(Collection<ResponseMessage> helps)
            throws ReliableDataStorageException, VersionConflictException {
        detectVersionConflictRejection(helps);

        if (notEnoughWrites(helps)) {
            throw new ReliableDataStorageException(
                    "Not enough members participated this reliable PUT operation - expected["
                            + expectedWrites + "] : received[" + helps.size()
                            + "]");
        }
    }

    private void detectVersionConflictRejection(
            Collection<ResponseMessage> helps) throws VersionConflictException {
        for (ResponseMessage responseMessage : helps) {
            if (responseMessage instanceof VersionConflictRejection) {
                throw new VersionConflictException(
                        "Distributed version conflict detected from ["
                                + responseMessage.getSource() + "]");
            }
        }
    }

    private boolean notEnoughWrites(Collection<ResponseMessage> helps) {
        return helps == null || helps.size() < expectedWrites;
    }

    public void putLocally(String context, Data data) throws IOException {
        try {
            localDataBank.put(context, data);
        } catch (VersionConflictException e) {
            throw new IOException(e);
        }
    }

    public Data getLocally(String context, Long keyHash) throws IOException {
        return localDataBank.get(context, keyHash);
    }

    public void deleteLocally(String storageContext, Long hashKey)
            throws IOException {
        localDataBank.delete(storageContext, hashKey);
    }

}
