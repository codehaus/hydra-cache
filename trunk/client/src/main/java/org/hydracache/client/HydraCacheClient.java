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
package org.hydracache.client;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public interface HydraCacheClient {
    /**
     * Retrieve the data using the given key. The key is hashed and a lookup is
     * performed to locate the node where the data resides. If it exists, it is
     * returned, otherwise null is returned.
     * 
     * @param key
     *            The key of the Data from the cache.
     * @return Object retrieved from the cache or null if not found
     * @throws IOException
     *             If there are problems with the connection
     * @throws OperationTimeoutException
     *             if we cannot locate the data in sufficient time.
     */
    public Object get(String key) throws IOException;

    /**
     * Add data to the cache with the given key.
     * 
     * @param key
     *            Identifies the data to put
     * @param object
     *            The object to be cached
     * @return A Future to return true once the operation has completed
     */
    public void put(String key, Serializable object);

}
