package org.hydracache.server.harmony.handler;

import org.apache.log4j.Logger;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.membership.MembershipRegistry;

public abstract class AbstractControlMessageHandler implements
        ControlMessageHandler {
    private static Logger log = Logger
            .getLogger(AbstractControlMessageHandler.class);

    protected Space space;

    protected MembershipRegistry membershipRegistry;

    public AbstractControlMessageHandler(Space space,
            MembershipRegistry membershipRegistry) {
        this.space = space;
        this.membershipRegistry = membershipRegistry;
    }

    protected boolean messageIsNotFromOurNeighbor(ControlMessage message) {
        return !membershipRegistry.isNeighbor(message.getSource());
    }

    @Override
    public void handle(ControlMessage message) throws Exception {
        if (messageIsNotFromOurNeighbor(message)) {
            if (log.isDebugEnabled())
                log.debug("Discarding message[" + message + "] from stranger");
            return;
        }

        doHandle(message);
    }

    protected abstract void doHandle(ControlMessage message) throws Exception;

}