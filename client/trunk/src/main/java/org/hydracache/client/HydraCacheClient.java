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

import org.hydracache.server.data.versioning.VersionConflictException;

/**
 * Represents the entry point for users to access the distributed cache.
 *
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public interface HydraCacheClient {
    /**
     * Retrieve the data using the given key. The key is hashed and a lookup is
     * performed to locate the node where the data resides. If it exists, it is
     * returned, otherwise null is returned.
     *
     * @param key The key of the Data from the cache.
     * @return Object retrieved from the cache or null if not found
     * @throws IOException               If there are problems with the connection
     * @throws OperationTimeoutException if we cannot locate the data in sufficient time.
     */
    public Object get(String key) throws Exception;

    /**
     * Retrieve the data using the given key associated the given storage
     * context. The key is hashed and a lookup is performed to locate the node
     * where the data resides. If it exists, it is returned, otherwise null is
     * returned.
     *
     * @param context The storage context that the data will be retrieved from
     * @param key     The key of the Data from the cache.
     * @return Object retrieved from the cache or null if not found
     * @throws IOException               If there are problems with the connection
     * @throws OperationTimeoutException if we cannot locate the data in sufficient time.
     */
    public Object get(String context, String key) throws Exception;

    /**
     * Delete the data using the given key.
     *
     * @param key The key of the Data from the cache.
     * @throws IOException               If there are problems with the connection
     * @throws OperationTimeoutException if we cannot locate the data in sufficient time.
     */
    public boolean delete(String key) throws Exception;

    /**
     * Delete the data using the given key associated the given storage context.
     *
     * @param context The storage context that the data will be deleted from
     * @param key     The key of the Data from the cache.
     * @throws IOException               If there are problems with the connection
     * @throws OperationTimeoutException if we cannot locate the data in sufficient time.
     */
    public boolean delete(String context, String key) throws Exception;

    /**
     * Add data to the cache with the given key.
     *
     * @param key    Identifies the data to put
     * @param object The object to be cached
     * @return A Future to return true once the operation has completed
     * @throws VersionConflictException thrown if version conflict has been detected
     */
    public void put(String key, Serializable object) throws Exception,
            VersionConflictException;

    /**
     * Add data to the cache with the given key associated the given storage
     * context.
     *
     * @param context The storage context that the data will be attached to
     * @param key     Identifies the data to put
     * @param object  The object to be cached
     * @return A Future to return true once the operation has completed
     * @throws VersionConflictException thrown if version conflict has been detected
     */
    public void put(String context, String key, Serializable object)
            throws Exception;

    /**
     * Stop this client instance and release any resource it holds. Once a
     * client has been shutdown it can not be used again, instead a new
     * instance should be created.
     *
     * @throws Exception if anything goes wrong
     */
    public void shutdown() throws Exception;

    /**
     * Check if the client instance of a running instance or if it has
     * already been shutdown
     *
     * @return if the client is running or not
     */
    public boolean isRunning();
}
