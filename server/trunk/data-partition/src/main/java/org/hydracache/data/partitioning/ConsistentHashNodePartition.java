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

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.partition.ConsistentHashable;
import org.hydracache.data.partition.ConsistentHashableString;
import org.hydracache.server.Identity;

/**
 * <p>
 * Consistent hash implementation using a supplied hashFunction. The hash
 * function can be one of the provided implementations or client provided,
 * however it must be consistent.
 * </p>
 * <p/>
 * <p>
 * This implementation is loosely based on Tom White's blog on Consistent
 * Hashing.
 * </p>
 *
 * @author Tan Quach
 * @see <a
 *      href="http://weblogs.java.net/blog/tomwhite/archive/2007/11/consistent_hash.html">Consistent
 *      Hashing</a>
 * @since 1.0
 */
public class ConsistentHashNodePartition<T extends ConsistentHashable>
        implements NodePartition<T> {

    private static final int DEFAULT_NUMBER_OF_REPLICAS = 20;

    protected final HashFunction hashFunction;

    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();

    private int numberOfReplicas;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected final Lock readLock = readWriteLock.readLock();
    protected final Lock writeLock = readWriteLock.writeLock();

    public ConsistentHashNodePartition(HashFunction hashFunction,
                                       Collection<T> nodes, int numberOfReplicas) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        writeLock.lock();
        try {
            for (T node : nodes) {
                add(node);
            }
        } finally {
            writeLock.unlock();
        }
    }

    public ConsistentHashNodePartition(HashFunction hashFunction,
                                       Collection<T> nodes) {
        this(hashFunction, nodes, DEFAULT_NUMBER_OF_REPLICAS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partitioning.NodeCircle#add(java.lang.Object)
     */

    public void add(T node) {
        writeLock.lock();
        try {
            if (numberOfReplicas == 0) {
                circle.put(hashFunction.hash(node), node);
                return;
            }

            for (int i = 1; i <= numberOfReplicas; i++) {
                circle.put(replicatedNodeHash(node, i), node);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partitioning.NodePartition#get(java.lang.String)
     */

    public T get(String hashKey) {
        if (hashKey == null)
            return null;

        readLock.lock();
        try {
            long hash = hashFunction
                    .hash(new ConsistentHashableString(hashKey));

            return getByHash(hash);
        } finally {
            readLock.unlock();
        }
    }

    protected T getByHash(long hash) {
        if (circle.isEmpty())
            return null;

        if (!circle.containsKey(hash)) {
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.data.partitioning.NodePartition#contains(java.lang.Object)
     */

    @Override
    public boolean contains(T node) {
        readLock.lock();
        try {
            return circle.containsValue(node);
        } finally {
            readLock.unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.data.partitioning.NodeCircle#remove(java.lang.Object)
     */

    public void remove(T node) {
        writeLock.lock();
        try {
            if (numberOfReplicas == 0) {
                circle.remove(hashFunction.hash(node));
                return;
            }

            for (int i = 1; i <= numberOfReplicas; i++) {
                circle.remove(replicatedNodeHash(node, i));
            }
        } finally {
            writeLock.unlock();
        }
    }

    private long replicatedNodeHash(T node, int i) {
        return hashFunction.hash(new ConsistentHashableString(node
                .getConsistentValue()
                + "-" + i));
    }

    public Collection<T> getServers() {
        Set<T> servers = new HashSet<T>(circle.values());

        return servers;       
    }
}
