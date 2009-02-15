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
package org.hydracache.data.partitioning;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hydracache.data.hashing.HashFunction;

/**
 * <p>
 * Consistent hash implementation using a supplied hashFunction. The hash
 * function can be one of the provided implementations or client provided,
 * however it must be consistent.
 * </p>
 * 
 * <p>
 * This implementation is loosely based on Tom White's blog on Consistent
 * Hashing.
 * </p>
 * 
 * @see <a
 *      href="http://weblogs.java.net/blog/tomwhite/archive/2007/11/consistent_hash.html">Consistent
 *      Hashing</a>
 * @author Tan Quach
 * @since 1.0
 */
public class ConsistentHashNodePartition<T> implements NodePartition<T> {

    protected final HashFunction hashFunction;
    
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();

    public ConsistentHashNodePartition(HashFunction hashFunction,
            Collection<T> nodes) {
        this.hashFunction = hashFunction;
        for (T node : nodes) {
            add(node);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partitioning.NodeCircle#add(java.lang.Object)
     */
    public void add(T node) {
        circle.put(hashFunction.hash(node), node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partitioning.NodeCircle#get(java.lang.Object)
     */
    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }

        long hash = hashFunction.hash(key);

        return getByHash(hash);
    }

    protected T getByHash(long hash) {
        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partitioning.NodeCircle#remove(java.lang.Object)
     */
    public void remove(T node) {
        circle.remove(hashFunction.hash(node));
    }

}
