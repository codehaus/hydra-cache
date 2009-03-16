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

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.UUID;

import org.hydracache.concurrent.SimpleResultFuture;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.protocol.control.message.PutOperation;
import org.hydracache.protocol.control.message.PutOperationResponse;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.data.storage.Data;
import org.jgroups.Channel;
import org.jgroups.ChannelClosedException;
import org.jgroups.ChannelException;
import org.jgroups.ChannelNotConnectedException;
import org.jgroups.Message;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class JGroupsSpaceCoordinationTest {

    private static final int SUBSTANCE_SIZE = 3;

    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private Identity serverId;

    Collection<ResponseMessage> responses;

    @Before
    public void setup() throws Exception {
        serverId = new Identity(80);
    }

    @Test
    public void broadcastResponseShouldTriggerSend() throws Exception {
        final Channel channel = mockChannelForSend();

        MultiplexMessageReceiver multiplexMessageReceiver = context
                .mock(MultiplexMessageReceiver.class);

        final JGroupsSpace space = new JGroupsSpace(serverId, channel,
                new KetamaBasedHashFunction(), SUBSTANCE_SIZE);
        space.setMultiplexRecevier(multiplexMessageReceiver);

        final PutOperationResponse response = new PutOperationResponse(
                serverId, UUID.randomUUID());

        space.broadcast(response);

        context.assertIsSatisfied();
    }

    @Test
    public void shouldReceiveHelpResponseWhenRequestedFromNeighbor()
            throws Exception {
        final Channel channel = mockChannelForSend();

        final MultiplexMessageReceiver multiplexMessageReceiver = context
                .mock(MultiplexMessageReceiver.class);
        {
            addReceivForExp(multiplexMessageReceiver);
        }

        final JGroupsSpace space = new JGroupsSpace(serverId, channel,
                new KetamaBasedHashFunction(), SUBSTANCE_SIZE);
        space.setMultiplexRecevier(multiplexMessageReceiver);

        final PutOperation putOperation = new PutOperation(serverId, new Data());

        responses = space.broadcast(putOperation);

        assertEquals("Not all responses have been received", 1, responses
                .size());
    }

    private Channel mockChannelForSend() throws ChannelException,
            ChannelNotConnectedException, ChannelClosedException {
        final Channel channel = context.mock(Channel.class);
        {
            addSetReceiverExp(channel);

            addConnectExp(channel);

            addSendExp(channel);
        }
        return channel;
    }

    private void addSendExp(final Channel channel)
            throws ChannelNotConnectedException, ChannelClosedException {
        context.checking(new Expectations() {
            {
                one(channel).send(with(any(Message.class)));
            }
        });
    }

    private void addConnectExp(final Channel channel) throws ChannelException {
        context.checking(new Expectations() {
            {
                one(channel).connect(JGroupsSpace.DEFAULT_SPACE_NAME);
            }
        });
    }

    private void addSetReceiverExp(final Channel channel)
            throws ChannelException {
        context.checking(new Expectations() {
            {
                one(channel).setReceiver(
                        with(any(MultiplexMessageReceiver.class)));
            }
        });
    }

    private void addReceivForExp(
            final MultiplexMessageReceiver multiplexMessageReceiver)
            throws UnknownHostException {
        context.checking(new Expectations() {
            {
                one(multiplexMessageReceiver).receiveFor(
                        with(any(PutOperation.class)));
                SimpleResultFuture<ResponseMessage> resultFuture = new SimpleResultFuture<ResponseMessage>();
                resultFuture.add(new PutOperationResponse(serverId, UUID
                        .randomUUID()));
                will(returnValue(resultFuture));
            }
        });
    }

}
