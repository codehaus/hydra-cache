package org.hydracache.server.harmony.jgroups;

import static org.junit.Assert.assertEquals;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.NodeSet;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

public class JgroupsMembershipRegistryTest {

    private JGroupsNode nodeA = new JGroupsNode(new Identity(8080), new IpAddress());

    @Test
    public void ensureMemberCanBeDeregisteredByJgroupAddress() {
        JgroupsMembershipRegistry registry = new JgroupsMembershipRegistry();

        registry.register(nodeA);
        registry.deregisterByJgroupAddress(nodeA.getJgroupsAddress());

        NodeSet nodes = registry.listAllMembers();

        assertEquals("Initial node set is not empty", 0, nodes.size());
    }

}
