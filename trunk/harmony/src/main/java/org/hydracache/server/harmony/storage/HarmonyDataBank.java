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

import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.hydracache.server.data.storage.DataStorageException;
import org.hydracache.server.harmony.core.Space;

/**
 * Harmony based {@link DataBank} implementation that implements the Quorum like
 * system
 * 
 * @author nzhu
 * 
 */
public class HarmonyDataBank implements DataBank {
    public static final int DEFAULT_W = 2;

    private DataBank localDataBank;

    private Space space;

    private int expectedWrites = DEFAULT_W;

    public HarmonyDataBank(DataBank localDataBank,
            ConflictResolver conflictResolver, Space space) {
        this.localDataBank = localDataBank;
        this.space = space;
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
    public Data get(Long keyHash) {
        return localDataBank.get(keyHash);
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
    public void put(Data data) throws DataStorageException {
        PutOperation putOperation = new PutOperation(space.getLocalNode()
                .getId(), data);

        try {
            Collection<ResponseMessage> helps = space.broadcast(putOperation);

            ensureReliablePut(helps);

            putLocally(data);
        } catch (IOException ex) {
            throw new DataStorageException(
                    "Failed to perform a reliable put operation", ex);
        }
    }

    private void ensureReliablePut(Collection<ResponseMessage> helps)
            throws ReliableDataStorageException {
        if (notEnoughPuts(helps)) {
            throw new ReliableDataStorageException(
                    "Not enough members participated this reliable put operation - expected["
                            + expectedWrites + "] : received[" + helps.size()
                            + "]");
        }
    }

    private boolean notEnoughPuts(Collection<ResponseMessage> helps) {
        return helps == null || helps.size() < expectedWrites;
    }

    public void putLocally(Data data) throws DataStorageException {
        localDataBank.put(data);
    }

    public Data getLocally(Long keyHash) {
        return localDataBank.get(keyHash);
    }

}
