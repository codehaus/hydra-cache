package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.GetOperationResponse;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

public class GetOperationHandler implements ControlMessageHandler {

    private Space space;

    private HarmonyDataBank harmonyDataBank;

    public GetOperationHandler(Space space, HarmonyDataBank harmonyDataBank) {
        this.space = space;
        this.harmonyDataBank = harmonyDataBank;
    }

    @Override
    public void handle(ControlMessage message) throws Exception {
        GetOperation getRequest = (GetOperation) message;
        
        Data currentData= harmonyDataBank.getLocally(getRequest.getHashKey());
        
        GetOperationResponse  response = new GetOperationResponse(space.getLocalNode().getId(), getRequest.getId(), currentData);
        
        space.broadcast(response);
    }

}
