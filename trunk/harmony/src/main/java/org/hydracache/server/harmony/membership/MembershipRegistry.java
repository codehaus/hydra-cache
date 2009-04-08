package org.hydracache.server.harmony.membership;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.lang.Validate;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.core.Space;

public class MembershipRegistry {
    protected ConcurrentSkipListSet<Node> registry = new ConcurrentSkipListSet<Node>();

    private ConcurrentHashMap<Identity, Boolean> neighborLookup = new ConcurrentHashMap<Identity, Boolean>();

    private Space space;

    public MembershipRegistry(Node self) {
        registry.add(self);
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public NodeSet listAllMembers() {
        return new NodeSet(registry.clone());
    }

    public void register(Node node) {
        registry.add(node);
        neighborLookup.clear();
    }

    public void deregister(Node node) {
        registry.remove(node);
        neighborLookup.clear();
    }

    public boolean contains(Node node) {
        return registry.contains(node);
    }

    public int size() {
        return registry.size();
    }

    public boolean isNeigbor(Node node) {
        Validate.notNull(node, "Node can not be null");

        Boolean isNeighbor = neighborLookup.get(node.getId());

        if (isNeighbor == null) {
            isNeighbor = space.findSubstancesForLocalNode().isNeighbor(
                    node.getId());
            neighborLookup.putIfAbsent(node.getId(), isNeighbor);
        }

        return isNeighbor;
    }
}
