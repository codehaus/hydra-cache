package org.hydracache.server.harmony.jgroups;

import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.Address;

public class JgroupsMembershipRegistry extends MembershipRegistry {
    private static Logger log = Logger
            .getLogger(JgroupsMembershipRegistry.class);

    public void deregisterByJgroupAddress(Address address) {
        Validate.notNull(address, "Address to be deregistered can not be null");

        Collection<Node> clonedMemberSet = registry.clone();

        for (Node node : clonedMemberSet) {
            if (notJgroupsNode(node)) {
                log.warn("Unknown node implementation: "
                        + node.getClass().getName());
                continue;
            }

            JGroupsNode jgroupsNode = (JGroupsNode) node;

            if (address.equals(jgroupsNode.getJgroupsAddress())) {
                deregister(jgroupsNode);
                break;
            }
        }
    }

    private boolean notJgroupsNode(Node node) {
        return !(node instanceof JGroupsNode);
    }

}
