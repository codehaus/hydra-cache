package org.hydracache.server.harmony.jgroups;

import static org.junit.Assert.assertEquals;

import org.hydracache.server.Identity;
import org.jgroups.stack.IpAddress;
import org.junit.Before;
import org.junit.Test;

public class JgroupsMembershipListenerTest {

    private JGroupsNode nodeA = new JGroupsNode(new Identity(8080),
            new IpAddress());
    private JgroupsMembershipRegistry registry = new JgroupsMembershipRegistry();

    @Before
    public void setup() {
        registry.register(nodeA);
    }

    @Test
    public void ensureNodeGetsRemovedWhenSuspectEventReceived() {
        JgroupsMembershipListener listener = new JgroupsMembershipListener(registry);

        listener.suspect(nodeA.getJgroupsAddress());

        assertEquals("Registry should be empty", 0, registry.listAllMembers()
                .size());
    }

}
