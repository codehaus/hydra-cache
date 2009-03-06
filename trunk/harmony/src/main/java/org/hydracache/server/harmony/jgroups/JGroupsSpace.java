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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.protocol.control.message.RequestMessage;
import org.hydracache.protocol.control.message.ResponseMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodePartitionSubstance;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.core.Space;
import org.hydracache.server.harmony.core.Substance;
import org.hydracache.server.harmony.core.SubstanceSet;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.Message;

/**
 * Basic JGroups based HOP space implementation
 * 
 * @author nzhu
 * 
 */
public class JGroupsSpace implements Space {

    private static final int DEFAULT_RECEIVE_TIMEOUT = 3000;

    public static final String DEFAULT_SPACE_NAME = "HydraSpace";

    private static Logger log = Logger.getLogger(JGroupsSpace.class);

    private MultiplexMessageReceiver multiplexRecevier;

    private Channel channel;

    private Identity serverId;

    private int substanceSize;

    private HashFunction hashFunction;

    private MembershipRegistry membershipRegistry;

    public JGroupsSpace(Identity serverId, Channel channel,
            HashFunction hashFunction, int substanceSize)
            throws ChannelException {
        this.channel = channel;
        this.serverId = serverId;
        this.substanceSize = substanceSize;
        this.hashFunction = hashFunction;

        log.info("Creating space[" + DEFAULT_SPACE_NAME + "] ... ");

        this.channel.connect(DEFAULT_SPACE_NAME);

        log.info("Space[" + DEFAULT_SPACE_NAME + "] created.");
    }

    public void setMultiplexRecevier(MultiplexMessageReceiver multiplexRecevier) {
        this.multiplexRecevier = multiplexRecevier;
        this.channel.setReceiver(multiplexRecevier);
    }

    public void setMembershipRegistry(MembershipRegistry membershipRegistry) {
        this.membershipRegistry = membershipRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Space#getLocalNode()
     */
    @Override
    public Node getLocalNode() {
        return new JGroupsNode(serverId, channel.getLocalAddress());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Space#getSubstances()
     */
    @Override
    public SubstanceSet findAllSubstances() {
        NodeSet allNodes = membershipRegistry.listAllMembers();
        SubstanceSet substances = new SubstanceSet();

        for (Node eachNode : allNodes) {
            Substance substance = new NodePartitionSubstance(eachNode,
                    allNodes, hashFunction, substanceSize);

            log.debug("Getting substance for node[" + eachNode + "]");

            substances.add(substance);
        }

        return substances;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.Space#findSubstancesForLocalNode()
     */
    @Override
    public SubstanceSet findSubstancesForLocalNode() {
        SubstanceSet allSubstances = findAllSubstances();
        SubstanceSet substances = new SubstanceSet();

        Node localNode = getLocalNode();

        for (Substance eachSubstance : allSubstances) {
            NodeSet neighbours = eachSubstance.getNeighbours();

            log.debug("Scanning substance neighbourhood [" + neighbours
                    + "] owner[" + eachSubstance.getOwner() + "]");

            if (neighbours.contains(localNode)) {
                log.debug("Getting substance [" + eachSubstance
                        + "] for local node[" + localNode + "]");

                substances.add(eachSubstance);
            }
        }

        return substances;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.harmony.core.Space#broadcast(org.hydracache.protocol
     * .control.message.RequestMessage)
     */
    @Override
    public Collection<ResponseMessage> broadcast(RequestMessage request)
            throws IOException {

        log.debug("Sending operation request: " + request);

        Message message = new Message();

        message.setObject(request);

        Future<Collection<ResponseMessage>> responseFuture = multiplexRecevier
                .receiveFor(request);

        send(message);

        log.debug("Operation request sent");

        try {
            Collection<ResponseMessage> responseMessages = responseFuture.get(
                    DEFAULT_RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS);

            log.debug("Response messages received: " + responseMessages);

            List<ResponseMessage> results = new ArrayList<ResponseMessage>();

            for (ResponseMessage responseMessage : responseMessages) {
                results.add(responseMessage);
            }

            return results;
        } catch (Exception ex) {
            throw new IOException("Failed to request help for operation: "
                    + request, ex);
        }
    }

    private void send(Message message) throws IOException {
        log.debug("Sending message[" + message + "]");

        try {
            channel.send(message);
        } catch (ChannelException ex) {
            throw new IOException("Failed to request help for a put operation",
                    ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.harmony.core.Space#broadcast(org.hydracache.protocol
     * .control.message.ResponseMessage)
     */
    @Override
    public void broadcast(ResponseMessage response) throws IOException {
        Message message = new Message();

        message.setObject(response);

        send(message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.core.Space#close()
     */
    @Override
    public void close() {
        try {
            channel.close();
        } catch (Exception ex) {
            log.warn("Failed to close channel[" + channel + "] gracefully", ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.harmony.core.Space#isActive()
     */
    @Override
    public boolean isActive() {
        return channel.isConnected();
    }

}
