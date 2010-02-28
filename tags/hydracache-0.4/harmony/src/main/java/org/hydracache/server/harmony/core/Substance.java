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

import org.hydracache.server.Identity;

/**
 * Interface defining a HOP substance
 * 
 * @author nzhu
 * 
 */
public interface Substance {

    /**
     * Get the owner node of this substance
     * 
     * @return owner node
     */
    Node getOwner();

    /**
     * Get the size of this substance.
     * 
     * <p>
     * The size of a substance determines how many neighboring node it can
     * include. For example a size 2 substance will contain two neighboring
     * nodes plus the owner node hence 3 nodes in total.
     * 
     * @return the size of this substance
     */
    int getSize();

    /**
     * Get all neighboring nodes in this substance
     * 
     * @return a set of neighboring nodes
     */
    NodeSet getNeighbours();

    /**
     * Check to see if the given node is a neighbor node to the owner of this
     * substance
     * 
     * @param nodeId
     *            node to be tested
     * @return if the given node is a neighbor node to the owner of this
     *         substance
     */
    boolean isNeighbor(Identity nodeId);

}
