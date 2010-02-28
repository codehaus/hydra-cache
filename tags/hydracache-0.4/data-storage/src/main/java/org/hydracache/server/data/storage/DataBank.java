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
package org.hydracache.server.data.storage;

import java.io.IOException;
import java.util.Collection;

import org.hydracache.server.data.versioning.VersionConflictException;

/**
 * Data storage bank interface
 * 
 * @author nzhu
 * 
 */
public interface DataBank {
    String DEFAULT_CACHE_CONTEXT_NAME = "__hydra-default__";

    /**
     * Ptt the given data into the bank storage and associate it with the
     * specified context
     * 
     * @param context
     *            storage context
     * @param data
     *            data
     */
    void put(String context, Data data) throws IOException,
            VersionConflictException;

    /**
     * Retrieve data using the given key hash
     * 
     * @param context
     *            storage context
     * @param keyHash
     *            key
     * @return data instance
     */
    Data get(String context, Long keyHash) throws IOException;

    /**
     * Get all data contained in this data bank from all storage contexts
     * 
     * @return all data in this storage bank
     */
    Collection<Data> getAll() throws IOException;

}
