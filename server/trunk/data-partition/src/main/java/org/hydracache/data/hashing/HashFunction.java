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
package org.hydracache.data.hashing;

import org.hydracache.data.partition.ConsistentHashable;

/**
 * Provides a function signature for any client that wishes to provide a custom hashing function.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public interface HashFunction {
    /**
     * Compute the hash for a given key. The supplied key can be anything, but should be unique within the context of
     * the application.
     * 
     * @param obj The object to be hashed
     * @return A positive integer
     */
    long hash(ConsistentHashable obj);
}
