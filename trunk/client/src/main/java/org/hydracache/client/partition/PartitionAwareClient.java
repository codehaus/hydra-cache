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
package org.hydracache.client.partition;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.hydracache.client.ClientException;
import org.hydracache.client.HydraCacheAdminClient;
import org.hydracache.client.HydraCacheClient;
import org.hydracache.client.transport.ConflictStatusHandler;
import org.hydracache.client.transport.DefaultResponseMessageHandler;
import org.hydracache.client.transport.HttpTransport;
import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.ConsistentHashNodePartition;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.codec.ProtocolDecoder;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Manages a partition of nodes and uses an implementation of HydraCacheClient
 * to execute requests against the distributed cache.
 * 
 * @author Tan Quach
 * @since 1.0
 */
public class PartitionAwareClient implements HydraCacheClient, HydraCacheAdminClient, Observer {
    private static Logger log = Logger.getLogger(PartitionAwareClient.class);

    private static final String PUT = "put";
    private static final String GET = "get";

    private static final String IP = "ip";

    private static final String PORT = "port";

    private NodePartition<Identity> nodePartition;
    private ProtocolEncoder<DataMessage> protocolEncoder;
    private ProtocolDecoder<DataMessage> protocolDecoder;
    private IncrementVersionFactory versionFactory;
    private Transport transport;

    // FIXME: use a weak but thread safe map here to avoid memory leak
    ConcurrentMap<String, Version> versionMap;

    private List<Identity> seedServerIds;

    /**
     * Construct a client instance referencing an existing {@link NodePartition}
     * 
     * @param connection
     *            The connection to the {@link Substance}.
     */
    public PartitionAwareClient(List<Identity> seedServerIds) {
        this(seedServerIds, new HttpTransport());
    }

    public PartitionAwareClient(List<Identity> seedServerIds, Transport transport) {
        this.seedServerIds = seedServerIds;
        this.transport = transport;
        
        this.nodePartition = new ConsistentHashNodePartition<Identity>(
                new KetamaBasedHashFunction(), seedServerIds);
        
        versionMap = new ConcurrentHashMap<String, Version>();
        versionFactory = new IncrementVersionFactory(new IdentityMarshaller());
        protocolEncoder = new DefaultProtocolEncoder(new MessageMarshallerFactory(versionFactory));
        protocolDecoder = new DefaultProtocolDecoder(new MessageMarshallerFactory(versionFactory));

        registerDefaultHandlers();
        
        // Register listeners for partition updates
        // TODO Make the interval configurable
        PartitionUpdatesPoller poller = new PartitionUpdatesPoller(180000, this, this);
        poller.start();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#get(java.lang.String)
     */
    @Override
    public synchronized Object get(final String key) throws Exception {
        Identity identity = nodePartition.get(key);

        transport.establishConnection(identity.getAddress().getHostName(), identity.getPort());
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(GET);
        requestMessage.setPath(key);

        ResponseMessage responseMessage = transport.sendRequest(requestMessage);
        transport.cleanUpConnection();
         
        Object object = null;
        if (responseMessage != null) {
            DataMessage dataMessage = protocolDecoder.decode(new DataInputStream(new ByteArrayInputStream(responseMessage.getResponseBody())));
            updateVersion(key, dataMessage);
            object = SerializationUtils.deserialize(dataMessage.getBlob());
        }
        return object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#put(java.lang.String,
     * java.io.Serializable)
     */
    @Override
    public synchronized void put(String key, Serializable data)
            throws Exception {
        Identity identity = nodePartition.get(key);
        Buffer buffer = serializeData(key, data);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(PUT);
        requestMessage.setPath(key);
        requestMessage.setRequestData(buffer);
        
        transport.establishConnection(identity.getAddress().getHostName(), identity.getPort());
        ResponseMessage responseMessage = transport.sendRequest(requestMessage);
        transport.cleanUpConnection();

        assert responseMessage != null && responseMessage.isSuccessful();
    }

    /* (non-Javadoc)
     * @see org.hydracache.client.HydraCacheAdminClient#listNodes()
     */
    @Override
    public synchronized List<Identity> listNodes() throws Exception {
        log.info("Retrieving list of nodes.");
        
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(GET);
        requestMessage.setPath("registry");

        // Pick a random node to connect to
        Random rnd = new Random();
        int nextInt = rnd.nextInt(seedServerIds.size());
        Identity identity = seedServerIds.get(nextInt);

        transport.establishConnection(identity.getAddress().getHostAddress(), identity.getPort());
        ResponseMessage responseMessage = transport.sendRequest(requestMessage);
        if (responseMessage == null)
            throw new ClientException("Failed to retrieve node registry.");
        
        List<Identity> identities = new LinkedList<Identity>();
        
        String registry = new String(responseMessage.getResponseBody());
        log.debug("Received registry: " + registry);
        JSONArray seedServerArray = new JSONArray(registry);
        for (int i = 0; i < seedServerArray.length(); i++) {
            JSONObject seedServer = seedServerArray.getJSONObject(i);

            identities.add(new Identity(InetAddress.getByName(seedServer
                    .getString(IP)), seedServer.getInt(PORT)));
        }
        return identities;
    }

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable o, Object arg) {
        log.info("Updating node partition");
        List<Identity> servers = (List<Identity>) arg;

        this.seedServerIds = servers;
        
        // Update the partition
        this.nodePartition = new ConsistentHashNodePartition<Identity>(
                new KetamaBasedHashFunction(), servers);
    }
    
    /**
     * @param key
     * @param data
     * @return
     * @throws IOException
     */
    private Buffer serializeData(String key, Serializable data) throws IOException {
        DataMessage dataMessage = new DataMessage();
        dataMessage.setBlob(SerializationUtils.serialize(data));
        Version version = versionMap.get(key);
        
        if (version == null)
            version = versionFactory.createNull();
        
        dataMessage.setVersion(version);
        
        Buffer buffer = Buffer.allocate();
        protocolEncoder.encode(dataMessage, buffer.asDataOutpuStream());
        return buffer;
    }

    // FIXME: implement weak map to avoid memory leak
    private void updateVersion(String key, DataMessage dataMessage) {
        versionMap.put(key, dataMessage.getVersion());
    }

    private void registerDefaultHandlers() {
        transport.registerHandler(HttpStatus.SC_CONFLICT, new ConflictStatusHandler());
        transport.registerHandler(HttpStatus.SC_OK, new DefaultResponseMessageHandler());
        transport.registerHandler(HttpStatus.SC_CREATED, new DefaultResponseMessageHandler());
    }
}
