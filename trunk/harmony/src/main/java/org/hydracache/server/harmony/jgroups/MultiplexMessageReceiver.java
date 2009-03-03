/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hydracache.server.harmony.jgroups;

import java.util.Collection;
import java.util.concurrent.Future;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.HeartBeat;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.handler.ControlMessageHandler;
import org.hydracache.server.harmony.handler.HeartBeatHandler;
import org.hydracache.server.harmony.handler.PutOperationHandler;
import org.hydracache.server.harmony.handler.ResponseHandler;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.harmony.util.RequestRegistry;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

/**
 * {@link MessageListener} implementation capable of handling multiple sets of
 * request/response simultaneously
 * 
 * @author nzhu
 * 
 */
public class MultiplexMessageReceiver extends ReceiverAdapter {

    private static Logger log = Logger
            .getLogger(MultiplexMessageReceiver.class);

    private Space space;

    private final JgroupsMembershipListener membershipListener;

    private final RequestRegistry requestRegistry = new RequestRegistry();

    private final int expectedResponses;

    private final ControlMessageHandler putOperationHandler;

    private final ControlMessageHandler responseHandler;

    private final HeartBeatHandler heartBeatHandler;

    public MultiplexMessageReceiver(Space space,
            JgroupsMembershipRegistry membershipRegistry,
            HarmonyDataBank dataBank, ConflictResolver conflictResolver,
            int expectedResponses) {
        this.space = space;
        this.membershipListener = new JgroupsMembershipListener(
                membershipRegistry);
        this.expectedResponses = expectedResponses;
        this.putOperationHandler = new PutOperationHandler(space, dataBank,
                conflictResolver);
        this.responseHandler = new ResponseHandler(requestRegistry);
        this.heartBeatHandler = new HeartBeatHandler(membershipRegistry);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.ReceiverAdapter#receive(org.jgroups.Message)
     */
    @Override
    public void receive(Message msg) {
        Validate.notNull(msg, "Response message can not be null");

        Object payload = msg.getObject();

        log.debug("Received message: " + msg + " with object: " + payload);

        Validate.isTrue(payload instanceof ControlMessage);

        ControlMessage controlMessage = (ControlMessage) payload;

        handleControlMessage(controlMessage);
    }

    void handleControlMessage(ControlMessage controlMessage) {
        if (fromLocalNode(controlMessage))
            return;

        try {
            if (controlMessage instanceof ResponseMessage) {
                responseHandler.handle(controlMessage);
                return;
            }

            if (controlMessage instanceof PutOperation) {
                putOperationHandler.handle(controlMessage);
                return;
            }

            if (controlMessage instanceof HeartBeat) {
                heartBeatHandler.handle(controlMessage);
                return;
            }

            log.warn("Ignoring unsupported control message received: "
                    + controlMessage);
        } catch (Exception ex) {
            log.error("Failed to handle message: " + controlMessage, ex);
        }
    }

    private boolean fromLocalNode(ControlMessage controlMessage) {
        return space.getLocalNode().getId().equals(controlMessage.getSource());
    }

    /**
     * Receive all available responses for the given request
     * 
     * @param request
     *            request for the responses
     * @return all available responses
     */
    public Future<Collection<ResponseMessage>> receiveFor(ControlMessage request) {
        Validate.notNull(request, "Request can not be null");

        log.debug("Registering request: " + request);

        SimpleResultFuture<ResponseMessage> resultFuture = new SimpleResultFuture<ResponseMessage>(
                expectedResponses);

        requestRegistry.register(request.getId(), resultFuture);

        return resultFuture;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.ReceiverAdapter#block()
     */
    @Override
    public void block() {
        membershipListener.block();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.ReceiverAdapter#suspect(org.jgroups.Address)
     */
    @Override
    public void suspect(Address suspectedMember) {
        membershipListener.suspect(suspectedMember);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jgroups.ReceiverAdapter#viewAccepted(org.jgroups.View)
     */
    @Override
    public void viewAccepted(View newView) {
        membershipListener.viewAccepted(newView);
    }

}
