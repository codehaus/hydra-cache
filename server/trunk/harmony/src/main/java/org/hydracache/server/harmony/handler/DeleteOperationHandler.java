/*
 * Copyright 2010 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hydracache.server.harmony.handler;

import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.DeleteOperation;
import org.hydracache.protocol.control.message.DeleteOperationResponse;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.hydracache.server.harmony.storage.HarmonyDataBank;

/**
 * Handler class for HOP DELETE operation
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class DeleteOperationHandler extends AbstractControlMessageHandler {

    private HarmonyDataBank harmonyDataBank;

    public DeleteOperationHandler(Space space,
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
        DeleteOperation operation = (DeleteOperation) message;

        String storageContext = operation.getContext();

        harmonyDataBank.deleteLocally(storageContext, operation.getHashKey());

        DeleteOperationResponse response = new DeleteOperationResponse(space
                .getLocalNode().getId(), operation.getId());

        space.broadcast(response);
    }

}
