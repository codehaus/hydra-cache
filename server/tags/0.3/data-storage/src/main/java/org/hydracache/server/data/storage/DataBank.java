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

import java.util.Collection;

/**
 * Data storage bank interface
 * 
 * @author nzhu
 * 
 */
public interface DataBank {

    /**
     * Put the given data into the bank storage
     * 
     * @param data
     *            data
     */
    void put(Data data) throws DataStorageException;

    /**
     * Retrieve data using the given key hash
     * 
     * @param keyHash
     *            key
     * @return data instance
     */
    Data get(Long keyHash);

    Collection<Data> getAll();

}
