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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.DeleteOperation;
import org.hydracache.protocol.control.message.GetOperation;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.data.resolver.ConflictResolver;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.handler.ControlMessageHandler;
import org.hydracache.server.harmony.handler.DeleteOperationHandler;
import org.hydracache.server.harmony.handler.GetOperationHandler;
import org.hydracache.server.harmony.handler.HeartBeatHandler;
import org.hydracache.server.harmony.handler.PutOperationHandler;
import org.hydracache.server.harmony.handler.ResponseHandler;
import org.hydracache.server.harmony.health.HeartBeat;
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

    private final Map<Class<?>, ControlMessageHandler> messageHandlerMap;

    public MultiplexMessageReceiver(Space space,
            JgroupsMembershipRegistry membershipRegistry,
            HarmonyDataBank harmonyDataBank, ConflictResolver conflictResolver,
            int expectedResponses) {
        this.space = space;
        this.membershipListener = new JgroupsMembershipListener(
                membershipRegistry);
        this.expectedResponses = expectedResponses;

        Map<Class<?>, ControlMessageHandler> tmpHandlerMap = contructControlMessageHandlerMap(
                space, membershipRegistry, harmonyDataBank, conflictResolver);

        messageHandlerMap = Collections.unmodifiableMap(tmpHandlerMap);
    }

    private Map<Class<?>, ControlMessageHandler> contructControlMessageHandlerMap(
            Space space, JgroupsMembershipRegistry membershipRegistry,
            HarmonyDataBank harmonyDataBank, ConflictResolver conflictResolver) {
        Map<Class<?>, ControlMessageHandler> tmpHandlerMap = new HashMap<Class<?>, ControlMessageHandler>();

        tmpHandlerMap.put(GetOperation.class, new GetOperationHandler(space,
                membershipRegistry, harmonyDataBank));
        tmpHandlerMap.put(DeleteOperation.class, new DeleteOperationHandler(
                space, membershipRegistry, harmonyDataBank));
        tmpHandlerMap.put(ResponseMessage.class, new ResponseHandler(
                requestRegistry));
        tmpHandlerMap.put(PutOperation.class, new PutOperationHandler(space,
                membershipRegistry, harmonyDataBank, conflictResolver));
        tmpHandlerMap.put(HeartBeat.class, new HeartBeatHandler(
                membershipRegistry));

        return tmpHandlerMap;
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

        if (log.isDebugEnabled())
            log.debug("Received message: " + msg + " with object: " + payload);

        Validate.isTrue(payload instanceof ControlMessage);

        ControlMessage controlMessage = (ControlMessage) payload;

        handleControlMessage(controlMessage);
    }

    void handleControlMessage(ControlMessage controlMessage) {
        if (fromLocalNode(controlMessage))
            return;

        try {
            ControlMessageHandler handler = null;

            // consider the message handler map is relatively small
            // this simple implementation is probably sufficiently fast
            for (Class<?> clazz : messageHandlerMap.keySet()) {
                if (clazz.isInstance(controlMessage)) {
                    handler = messageHandlerMap.get(clazz);
                    break;
                }
            }

            if (handler != null) {
                handler.handle(controlMessage);
            } else {
                log.warn("Ignoring unsupported control message received: "
                        + controlMessage);
            }
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

        if (log.isDebugEnabled())
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
