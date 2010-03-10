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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.concurrent.Future;

import org.hydracache.protocol.control.message.ControlMessage;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.data.resolver.ArbitraryResolver;
import org.hydracache.server.harmony.AbstractMockeryTest;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.health.HeartBeat;
import org.hydracache.server.harmony.storage.HarmonyDataBank;
import org.hydracache.server.harmony.test.TestDataGenerator;
import org.jgroups.Message;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class MultiplexMessageReceiverTest extends AbstractMockeryTest {

    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private static final Identity NEIGHBOR_SOURCE_ID = new Identity(8081);

    private static final int JOIN_WAIT = 200;

    private final Node localNode = new JGroupsNode(new Identity(8080),
            new IpAddress());

    private final JgroupsMembershipRegistry membershipRegistry = context
            .mock(JgroupsMembershipRegistry.class);

    private Collection<ResponseMessage> responses;

    @Test
    public void ensureUnknowControlMessageDoesNotCauseException()
            throws Exception {
        int expectedNumOfResps = 3;

        final Space space = context.mock(Space.class);

        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                space, membershipRegistry, dataBank, new ArbitraryResolver(),
                expectedNumOfResps);

        final ControlMessage request = new ControlMessage(NEIGHBOR_SOURCE_ID) {
            private static final long serialVersionUID = 1L;
        };

        receiver.receiveFor(request);

        context.assertIsSatisfied();
    }

    @Test
    public void responseMsgShouldBeMappedToRequest() throws Exception {
        int expectedNumOfResps = 3;

        final Space space = context.mock(Space.class);

        {
            addGetLocalNodeExp(context, space);
        }

        final HarmonyDataBank dataBank = context.mock(HarmonyDataBank.class);

        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                space, membershipRegistry, dataBank, new ArbitraryResolver(),
                expectedNumOfResps);

        final PutOperation request = new PutOperation(NEIGHBOR_SOURCE_ID,
                "testContext", TestDataGenerator.createRandomData());

        Future<Collection<ResponseMessage>> responseFuture = receiver
                .receiveFor(request);

        receiveResponse(receiver, request);
        receiveResponse(receiver, request);
        receiveResponse(receiver, request);

        Thread.sleep(JOIN_WAIT);

        responses = responseFuture.get();

        assertNotNull("Resonse should not be null", responses);
        assertEquals("Response size is incorrect", expectedNumOfResps,
                responses.size());

        context.assertIsSatisfied();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestShouldBeRejected() {
        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                null, null, null, new ArbitraryResolver(), 1);

        receiver.receiveFor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullResponseShouldBeRejected() {
        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                null, null, null, new ArbitraryResolver(), 1);

        receiver.receive(null);
    }

    @Test
    public void messageFromMyselfShouldBeIgnored() {

        final Space space = context.mock(Space.class);

        context.checking(new Expectations() {
            {
                one(space).getLocalNode();
                will(returnValue(localNode));
            }
        });

        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                space, null, null, new ArbitraryResolver(), 1);

        HeartBeat message = new HeartBeat(localNode);

        receiver.handleControlMessage(message);

        context.assertIsSatisfied();
    }

    private static void receiveResponse(
            final MultiplexMessageReceiver receiver, final PutOperation request)
            throws Exception {
        ResponseMessage response = new PutOperationResponse(NEIGHBOR_SOURCE_ID,
                request.getId());
        Message responseMsg = new Message();

        responseMsg.setObject(response);

        receiver.receive(responseMsg);
    }

}
