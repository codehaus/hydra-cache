package org.hydracache.server.harmony.membership;

import java.util.concurrent.ConcurrentSkipListSet;

import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;

public class MembershipRegistry {
    protected ConcurrentSkipListSet<Node> registry = new ConcurrentSkipListSet<Node>();

    public MembershipRegistry(Node self) {
        registry.add(self);
    }

    public NodeSet listAllMembers() {
        return new NodeSet(registry.clone());
    }

    public void register(Node node) {
        registry.add(node);
    }

    public void deregister(Node node) {
        registry.remove(node);
    }

    public boolean contains(Node node) {
        return registry.contains(node);
    }

    public int size() {
        return registry.size();
    }

}
