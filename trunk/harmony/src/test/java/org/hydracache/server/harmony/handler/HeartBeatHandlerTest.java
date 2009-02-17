package org.hydracache.server.harmony.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.hydracache.protocol.control.message.HeartBeat;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.jgroups.JgroupsMembershipRegistry;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

public class HeartBeatHandlerTest {
    private Node self = new JGroupsNode(new Identity(8080), new IpAddress(
            7000));
    private Node source = new JGroupsNode(new Identity(8081), new IpAddress(
            7000));

    @Test
    public void ensureHeartBeatTriggerMemberRegistration() throws Exception {
        JgroupsMembershipRegistry registry = new JgroupsMembershipRegistry(self);

        HeartBeat heartBeat = new HeartBeat(source);

        HeartBeatHandler heartBeatHandler = new HeartBeatHandler(registry);

        heartBeatHandler.handle(heartBeat);

        assertTrue("Registry should contain the source node", registry
                .contains(source));
    }

    public void ensureNoDuplicatedRegistration() throws Exception {
        JgroupsMembershipRegistry registry = new JgroupsMembershipRegistry(self);

        HeartBeat heartBeat = new HeartBeat(source);

        HeartBeatHandler heartBeatHandler = new HeartBeatHandler(registry);

        heartBeatHandler.handle(heartBeat);
        heartBeatHandler.handle(heartBeat);
        heartBeatHandler.handle(heartBeat);

        assertEquals("Registry size is incorrect", 1, registry.size());
    }

}
