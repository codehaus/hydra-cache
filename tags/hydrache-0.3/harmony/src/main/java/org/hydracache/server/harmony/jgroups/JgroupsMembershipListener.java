package org.hydracache.server.harmony.jgroups;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.MembershipListener;
import org.jgroups.View;

public class JgroupsMembershipListener implements MembershipListener {
    private static Logger log = Logger
            .getLogger(JgroupsMembershipListener.class);

    private JgroupsMembershipRegistry membershipRegistry;

    public JgroupsMembershipListener(
            JgroupsMembershipRegistry membershipRegistry) {
        this.membershipRegistry = membershipRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.MembershipListener#block()
     */
    @Override
    public void block() {
        log.debug("Block message received");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.MembershipListener#suspect(org.jgroups.Address)
     */
    @Override
    public void suspect(Address suspectedMember) {
        log.info("New suspect member [" + suspectedMember + "] received");

        membershipRegistry.deregisterByJgroupAddress(suspectedMember);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.MembershipListener#viewAccepted(org.jgroups.View)
     */
    @Override
    public void viewAccepted(View newView) {
        log.debug("New view receive: " + newView);
    }

}
