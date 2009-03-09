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

/**
 * <p>
 * A consistent hashing implementation which allows a client to partition a set
 * of nodes. The distribution may or may not be uniform depending on the
 * implementation.
 * </p>
 * 
 * <p>
 * From Tom White's blog: <blockquote>
 * <p>"The circle is represented as a sorted map of integers, which represent
 * the hash values, to caches (of type T here). When a ConsistentHash object is
 * created each node is added to the circle map a number of times (controlled by
 * numberOfReplicas). The location of each replica is chosen by hashing the
 * node's name along with a numerical suffix, and the node is stored at each of
 * these points in the map.
 * </p>
 * <p>
 *"To find a node for an object (the get method), the hash value of the object
 * is used to look in the map. Most of the time there will not be a node stored
 * at this hash value (since the hash value space is typically much larger than
 * the number of nodes, even with replicas), so the next node is found by
 * looking for the first key in the tail map. If the tail map is empty then we
 * wrap around the circle by getting the first key in the circle."
 * </p>
 * </blockquote>
 * 
 * @see <a href="http://allthingsdistributed.com/2007/10/amazons_dynamo.html">Werner Vogel on Amazon's Dynamo</a>
 * @see <a href="http://www8.org/w8-papers/2a-webserver/caching/paper2.html">Web Caching with Consistent Hashing</a>
 * @author Tan Quach
 * @since 1.0
 */
public interface NodePartition<T> {

    /**
     * Add a node to the circle. The hash function used to determine where on
     * the clock face the node should always return the same value for this
     * particular node.
     * 
     * @param node
     *            A new node on the circle.
     */
    public void add(T node);

    /**
     * Take out a node from the circle.
     * 
     * @param node
     *            The to-be removed node
     */
    public void remove(T node);

    /**
     * Given a key, return the node T that this key hashes to. Keys, like nodes,
     * must consistently hash to the same value.
     * 
     * @param key
     *            The data key
     * @return The node where the key should consistently hash to. If the node
     *         goes down, the method should return the node's nearest neighbour
     *         going in a "clockwise" direction.
     */
    public T get(String key);
}
