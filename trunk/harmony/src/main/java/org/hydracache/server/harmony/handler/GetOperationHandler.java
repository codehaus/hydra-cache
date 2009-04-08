package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

public class GetOperationHandler extends AbstractControlMessageHandler {

    private HarmonyDataBank harmonyDataBank;

    public GetOperationHandler(Space space,
            MembershipRegistry membershipRegistry,
            HarmonyDataBank harmonyDataBank) {
        super(space, membershipRegistry);
        this.harmonyDataBank = harmonyDataBank;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.harmony.handler.AbstractControlMessageHandler#doHandle
     * (org.hydracache.protocol.control.message.ControlMessage)
     */
    @Override
    protected void doHandle(ControlMessage message) throws Exception {
        GetOperation getRequest = (GetOperation) message;

        Data currentData = harmonyDataBank.getLocally(getRequest.getHashKey());

        if (currentData != null) {
            GetOperationResponse response = new GetOperationResponse(space
                    .getLocalNode().getId(), getRequest.getId(), currentData);

            space.broadcast(response);
        }
    }

}
