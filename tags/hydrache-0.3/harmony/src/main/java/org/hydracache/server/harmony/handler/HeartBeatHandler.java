package org.hydracache.server.harmony.handler;

import org.apache.commons.lang.Validate;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.HeartBeat;
import org.hydracache.server.harmony.membership.MembershipRegistry;

public class HeartBeatHandler implements ControlMessageHandler {
    private MembershipRegistry membershipRegistry;

    public HeartBeatHandler(MembershipRegistry membershipRegistry) {
        this.membershipRegistry = membershipRegistry;
    }

    @Override
    public void handle(ControlMessage message) throws Exception {
        Validate.isTrue(message instanceof HeartBeat,
                "Can't handle unsupported message: " + message);

        HeartBeat heartBeat = (HeartBeat) message;

        membershipRegistry.register(heartBeat.getSourceNode());
    }

}
