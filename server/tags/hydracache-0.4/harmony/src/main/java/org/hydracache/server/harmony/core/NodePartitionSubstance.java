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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.data.partitioning.SubstancePartition;
import org.hydracache.server.Identity;

/**
 * {@link Substance} implementation using {@link NodePartition}
 * 
 * @author nzhu
 * 
 */
public class NodePartitionSubstance implements Substance {

    private Node owner;

    private Map<Identity, Node> nodeMap = new HashMap<Identity, Node>();

    private SubstancePartition partition;

    private int size;

    public NodePartitionSubstance(Node owner, NodeSet allNodes,
            HashFunction hashFunction, int size) {
        this.owner = owner;

        List<Identity> nodeIds = new ArrayList<Identity>();

        for (Node eachNode : allNodes) {
            nodeMap.put(eachNode.getId(), eachNode);
            nodeIds.add(eachNode.getId());
        }

        this.partition = new SubstancePartition(hashFunction, nodeIds);

        this.size = size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Substance#getOwner()
     */
    public Node getOwner() {
        return owner;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Substance#getSize()
     */
    public int getSize() {
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Substance#getNeighbours()
     */
    @Override
    public NodeSet getNeighbours() {
        NodeSet neighbours = new NodeSet();

        Identity currentNodeId = owner.getId();

        for (int i = 0; i < size; i++) {
            Identity nextNodeId = partition.next(currentNodeId);

            if (isNotOwnerItself(nextNodeId))
                neighbours.add(nodeMap.get(nextNodeId));

            currentNodeId = nextNodeId;
        }

        return neighbours;
    }

    private boolean isNotOwnerItself(Identity nextNodeId) {
        return !owner.getId().equals(nextNodeId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.harmony.core.Substance#isNeighbor(org.hydracache
     * .server.Identity)
     */
    @Override
    public boolean isNeighbor(Identity nodeId) {
        NodeSet neighbors = getNeighbours();
        boolean result = false;

        for (Node node : neighbors) {
            result = nodeId.equals(node.getId());

            if (result)
                break;
        }

        return result;
    }
}
