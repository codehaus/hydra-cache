package org.hydracache.server.harmony.membership;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

public class MembershipRegistryTest {

    private Node nodeA = new JGroupsNode(new Identity(8080), new IpAddress());
    private Node nodeA2 = new JGroupsNode(new Identity(8080), new IpAddress());
    private Node nodeB = new JGroupsNode(new Identity(8081), new IpAddress());

    @Test
    public void ensureInitialNodeSetIsNotEmpty() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        NodeSet nodes = registry.listAllMembers();

        assertNotNull("Node set is null", nodes);
        assertEquals("Initial node set is not empty", 1, nodes.size());
    }

    @Test
    public void ensureNodeCanBeRegistered() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.register(nodeA2);
        registry.register(nodeB);

        assertTrue("Registry should contain node A", registry.contains(nodeA));
        assertTrue("Registry should contain node A2", registry.contains(nodeA2));
        assertTrue("Registry should contain node B", registry.contains(nodeB));
    }

    @Test
    public void ensureDuplicationCanNotBeRegistered() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.register(nodeA);
        registry.register(nodeA2);

        assertEquals("Node set size is incorrect", 1, registry.size());
    }

    @Test
    public void ensureNodeCanBeDeregistered() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.deregister(nodeA);
        
        NodeSet nodes = registry.listAllMembers();

        assertEquals("Initial node set is not empty", 0, nodes.size());
    }
    
    @Test
    public void ensureNodeCanBeDeregisteredMulitpleTimes() {
        MembershipRegistry registry = new MembershipRegistry(nodeA);

        registry.deregister(nodeA);
        registry.deregister(nodeA);
        
        NodeSet nodes = registry.listAllMembers();

        assertEquals("Initial node set is not empty", 0, nodes.size());
    }

}
