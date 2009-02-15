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
package org.hydracache.server.harmony.core;

import java.util.Collection;

import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.server.Identity;

/**
 * A {@link Substance} friendly {@link NodePartition} implementation
 * 
 * @author nzhu
 * 
 */
public class SubstancePartition extends ConsistentHashNodePartition<Identity> {

    public SubstancePartition(HashFunction hashFunction,
            Collection<Identity> ids) {
        super(hashFunction, ids);
    }

    /**
     * Return the next {@link Identity} in the circle from the given id.
     * 
     * <p>
     * This method implementation is consistent therefore it always return the
     * same {@link Identity} if given the same {@link Identity}.
     * 
     * @param id
     *            given {@link Identity}
     * @return the next {@link Identity} from the given one in the circle
     */
    public Identity next(Identity id) {
        long hash = hashFunction.hash(id);

        hash++;

        return getByHash(hash);
    }

}
