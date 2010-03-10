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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.hydracache.data.hashing.NativeHashFunction;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.core.SubstanceSet;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * @author nzhu
 * 
 */
public class JGroupsSpaceMembershipTest {
    private Mockery context = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private JGroupsNode nodeA = new JGroupsNode(new Identity(8080),
            new IpAddress(7001));
    private JGroupsNode nodeB = new JGroupsNode(new Identity(8081),
            new IpAddress(7002));
    private JGroupsNode nodeC = new JGroupsNode(new Identity(8082),
            new IpAddress(7003));

    @Test
    public void ensureSpaceHealthCanBeChecked() throws Exception {
        final Channel channel = mockChannel();

        final JGroupsSpace space = createSpace(channel);

        assertTrue("Space should be active", space.isActive());

        space.close();

        assertFalse("Space should not be active", space.isActive());

        context.assertIsSatisfied();
    }

    @Test
    public void testGetAllSubstances() throws Exception {
        int expectedSubstances = 3;

        final JGroupsSpace space = createSpace(mockChannel());

        SubstanceSet substances = space.findAllSubstances();

        assertNotNull("Substances set is null", substances);

        assertEquals("Number of total substances is not correct",
                expectedSubstances, substances.size());

        context.assertIsSatisfied();
    }

    @Test
    public void testFindAllSubstancesForLocalNode() throws Exception {
        final JGroupsSpace space = createSpace(mockChannel());

        SubstanceSet substances = space.findSubstancesForLocalNode();

        assertNotNull("Substances set is null", substances);

        assertEquals("Local node should only belong to 1 substance", 1,
                substances.size());

        context.assertIsSatisfied();
    }

    private Channel mockChannel() throws ChannelException {
        Channel channel = context.mock(Channel.class);

        {
            addSetReceiverExp(channel);
            addConnectExp(channel);
            addGetLocalAddressExp(channel);

            addIsConnectedExp(channel);
            addCloseExp(channel);
            addIsNotConnectedExp(channel);
        }

        return channel;
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

    private void addGetLocalAddressExp(final Channel channel)
            throws ChannelException {
        context.checking(new Expectations() {
            {
                atMost(1).of(channel).getLocalAddress();
                will(returnValue(nodeA.getJgroupsAddress()));
            }
        });
    }

    private void addIsConnectedExp(final Channel channel) {
        context.checking(new Expectations() {
            {
                atMost(1).of(channel).isConnected();
                will(returnValue(true));
            }
        });
    }

    private void addCloseExp(final Channel channel) {
        context.checking(new Expectations() {
            {
                atMost(1).of(channel).close();
            }
        });
    }

    private void addIsNotConnectedExp(final Channel channel) {
        context.checking(new Expectations() {
            {
                atMost(1).of(channel).isConnected();
                will(returnValue(false));
            }
        });
    }

    private JGroupsSpace createSpace(Channel channel) throws ChannelException {
        final JGroupsSpace space = new JGroupsSpace(nodeA.getId(), channel,
                new NativeHashFunction(), 1);

        space.setMultiplexRecevier(mockMessageReceiver());

        space.setMembershipRegistry(mockMembershipRegistry());

        return space;
    }

    private MultiplexMessageReceiver mockMessageReceiver() {
        return context.mock(MultiplexMessageReceiver.class);
    }

    private MembershipRegistry mockMembershipRegistry() {
        final MembershipRegistry membershipRegistry = context
                .mock(MembershipRegistry.class);

        context.checking(new Expectations() {
            {
                atMost(1).of(membershipRegistry).listAllMembers();
                will(returnValue(new NodeSet(Arrays.asList(nodeA, nodeB, nodeC))));
            }
        });

        return membershipRegistry;
    }

}
