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

import org.hydracache.protocol.control.message.HeartBeat;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.handler.PutOperationHandlerTest;
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
public class MultiplexMessageReceiverTest {

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
    public void responseMsgShouldBeMappedToRequest() throws Exception {
        int expectedNumOfResps = 3;

        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                PutOperationHandlerTest.mockSpaceToRespond(context),
                membershipRegistry, PutOperationHandlerTest
                        .mockDataBankToPut(context), expectedNumOfResps);

        final PutOperation request = new PutOperation(NEIGHBOR_SOURCE_ID,
                TestDataGenerator.createRandomData());

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
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRequestShouldBeRejected() {
        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                null, null, null, 1);

        receiver.receiveFor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullResponseShouldBeRejected() {
        final MultiplexMessageReceiver receiver = new MultiplexMessageReceiver(
                null, null, null, 1);

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
                space, null, null, 1);

        HeartBeat message = new HeartBeat(localNode);

        receiver.handleControlMessage(message);

        context.assertIsSatisfied();
    }

    public static void receiveResponse(final MultiplexMessageReceiver receiver,
            final PutOperation request) throws Exception {
        ResponseMessage response = new PutOperationResponse(NEIGHBOR_SOURCE_ID,
                request.getId());
        Message responseMsg = new Message();
        responseMsg.setObject(response);

        receiver.receive(responseMsg);
    }

}
