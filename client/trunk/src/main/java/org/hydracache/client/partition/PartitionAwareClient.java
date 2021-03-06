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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.hydracache.client.EmptySpaceException;
import org.hydracache.client.HydraClientException;
import org.hydracache.client.HydraCacheAdminClient;
import org.hydracache.client.HydraCacheClient;
import org.hydracache.client.transport.HttpTransport;
import org.hydracache.client.transport.RequestMessage;
import org.hydracache.client.transport.ResponseMessage;
import org.hydracache.client.transport.Transport;
import org.hydracache.data.hashing.KetamaBasedHashFunction;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.data.partitioning.SubstancePartition;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.codec.ProtocolDecoder;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.marshaller.DataMessageMarshaller;
import org.hydracache.protocol.data.marshaller.DataMessageXmlMarshaller;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.IdentityXmlMarshaller;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.data.versioning.Version;
import org.hydracache.server.data.versioning.VersionXmlMarshaller;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Manages a partition of nodes and uses an implementation of HydraCacheClient
 * to execute requests against the distributed cache.
 * 
 * @author Tan Quach, Nick Zhu
 * @since 1.0
 */
public class PartitionAwareClient implements HydraCacheClient,
        HydraCacheAdminClient, Observer {
    private static final String VERSION_COMPOSITE_KEY_DIVIDER = "_::_";

    private static Logger log = Logger.getLogger(PartitionAwareClient.class);

    private static final String PUT = "put";

    private static final String GET = "get";

    private static final String DELETE = "delete";

    private static final String IP = "ip";

    private static final String PORT = "port";

    private AtomicBoolean running = new AtomicBoolean(false);

    private SubstancePartition nodePartition;
    private ProtocolEncoder<DataMessage> protocolEncoder;
    private ProtocolDecoder<DataMessage> protocolDecoder;
    private IncrementVersionFactory versionFactory;
    private Messenger messenger;

    private Map<String, Version> versionMap = Collections
            .synchronizedMap(new WeakHashMap<String, Version>());

    private List<Identity> seedServerIds;

    private PartitionUpdatesPoller poller;

    /**
     * Construct a client instance referencing an existing {@link NodePartition}
     */
    public PartitionAwareClient(List<Identity> seedServerIds,
            PartitionUpdatesPoller poller) {
        this(seedServerIds, new HttpTransport(), poller);
    }

    public PartitionAwareClient(List<Identity> seedServerIds,
            Transport transport, PartitionUpdatesPoller poller) {
        this.seedServerIds = seedServerIds;
        this.messenger = new Messenger(transport);

        createNodePartition(seedServerIds);

        versionMap = new ConcurrentHashMap<String, Version>();
        versionFactory = new IncrementVersionFactory(new IdentityMarshaller());
        protocolEncoder = new DefaultProtocolEncoder(
                createBinaryDataMsgMarshaller(), createXmlDataMsgMarshaller());
        protocolDecoder = new DefaultProtocolDecoder(
                createBinaryDataMsgMarshaller(), createXmlDataMsgMarshaller());

        running.set(true);

        // TODO: Review this threading model, a separate poller per instance
        // might not be ideal
        this.poller = poller;
        poller.start();
    }

    private void createNodePartition(List<Identity> seedServerIds) {
        this.nodePartition = new SubstancePartition(
                new KetamaBasedHashFunction(), seedServerIds);
    }

    private DataMessageXmlMarshaller createXmlDataMsgMarshaller() {
        return new DataMessageXmlMarshaller(new VersionXmlMarshaller(
                new IdentityXmlMarshaller(), versionFactory));
    }

    private DataMessageMarshaller createBinaryDataMsgMarshaller() {
        return new DataMessageMarshaller(versionFactory);
    }

    NodePartition<Identity> getNodePartition() {
        return nodePartition;
    }

    void setMessager(Messenger messenger) {
        this.messenger = messenger;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#delete(java.lang.String,
     * java.lang.String)
     */

    @Override
    public boolean delete(String context, String key) throws Exception {
        validateRunningState();

        Identity identity = findNodeByKey(key);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(DELETE);
        requestMessage.setPath(key);
        requestMessage.setContext(context);

        ResponseMessage responseMessage = sendMessage(identity, requestMessage);

        return responseMessage.isSuccessful();
    }

    Identity findNodeByKey(String key) {
        if (spaceIsEmpty(nodePartition)) {
            if (!seedServerIds.isEmpty()) {
                log.debug("Space is empty, fall back to recovery mode recreating space using seeds");
                createNodePartition(seedServerIds);
            }
        }

        Identity targetNode = nodePartition.get(key);

        guardNullIdentity(targetNode);

        return targetNode;
    }

    private void validateRunningState() {
        if (!isRunning())
            throw new IllegalStateException(
                    "Client instance has already been stopped, no operation is permitted");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#delete(java.lang.String)
     */

    @Override
    public boolean delete(String key) throws Exception {
        return delete(null, key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#get(java.lang.String,
     * java.lang.String)
     */

    @Override
    public Object get(String context, String key) throws Exception {
        validateRunningState();

        Identity identity = findNodeByKey(key);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(GET);
        requestMessage.setPath(key);
        requestMessage.setContext(context);

        ResponseMessage responseMessage = sendMessage(identity, requestMessage);

        Object object = null;

        if (responseMessage != null) {
            DataMessage dataMessage = protocolDecoder
                    .decode(new DataInputStream(new ByteArrayInputStream(
                            responseMessage.getResponseBody())));
            updateVersion(context, key, dataMessage);
            object = SerializationUtils.deserialize(dataMessage.getBlob());
        }

        return object;
    }

    private void guardNullIdentity(Identity identity) {
        if (identity == null) {
            throw new EmptySpaceException(
                    "No functional node has been detected in the Hydra space");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#get(java.lang.String)
     */

    @Override
    public Object get(final String key) throws Exception {
        return get(null, key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#put(java.lang.String,
     * java.lang.String, java.io.Serializable)
     */

    @Override
    public void put(String context, String key, Serializable data)
            throws Exception {
        validateRunningState();

        Identity identity = findNodeByKey(key);

        Buffer buffer = serializeData(context, key, data);

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(PUT);
        requestMessage.setPath(key);
        requestMessage.setContext(context);
        requestMessage.setRequestData(buffer);

        ResponseMessage responseMessage = sendMessage(identity, requestMessage);

        assert responseMessage != null && responseMessage.isSuccessful();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#put(java.lang.String,
     * java.io.Serializable)
     */

    @Override
    public void put(String key, Serializable data) throws Exception {
        put(null, key, data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheAdminClient#listNodes()
     */

    @Override
    public List<Identity> listNodes() throws Exception {
        ResponseMessage responseMessage = makeGetQuery("registry");

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

    private ResponseMessage makeGetQuery(String call) throws Exception {
        validateRunningState();

        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(GET);
        requestMessage.setPath(call);

        Identity identity = pickRandomServerFromRegistry();

        ResponseMessage responseMessage = messenger.sendMessage(identity,
                nodePartition, requestMessage);

        if (responseMessage == null)
            throw new HydraClientException("Failed to query server with call["
                    + call + "].");

        return responseMessage;
    }

    Identity pickRandomServerFromRegistry() {
        List<Identity> targetNodes;

        if (spaceIsEmpty(nodePartition)) {
            if (seedServerIds.isEmpty()) {
                throw new EmptySpaceException(
                        "Empty space detected, aborting query execution");
            } else {
                log.debug("Space is empty, fall back to recovery mode using seed list");
                targetNodes = seedServerIds;
            }
        } else {
            targetNodes = nodePartition.getNodes();
        }

        log.debug("Picking random server in: " + targetNodes);

        Random rnd = new Random();
        int nextInt = rnd.nextInt(targetNodes.size());
        Identity identity = targetNodes.get(nextInt);

        log.debug("Server [" + identity + "] has been selected");

        return identity;
    }

    private boolean spaceIsEmpty(SubstancePartition nodePartition) {
        List<Identity> targetNodes = nodePartition.getNodes();
        return targetNodes == null || targetNodes.isEmpty();
    }

    @Override
    public Map<String, String> getStorageInfo() throws Exception {
        ResponseMessage responseMessage = makeGetQuery("storage");

        String infoString = new String(responseMessage.getResponseBody());

        HashMap<String, String> infoMap = new HashMap<String, String>();

        JSONObject infoJsonObj = new JSONObject(infoString);

        for (Iterator<?> it = infoJsonObj.keys(); it.hasNext();) {
            String key = it.next().toString();
            infoMap.put(key, infoJsonObj.getString(key));
        }

        return infoMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable o, Object arg) {
        log.info("Updating node partition -> " + arg);

        List<Identity> servers = (List<Identity>) arg;

        createNodePartition(servers);
    }

    private Buffer serializeData(String context, String key, Serializable data)
            throws IOException {
        DataMessage dataMessage = new DataMessage();
        dataMessage.setBlob(SerializationUtils.serialize(data));

        attachVersion(context, key, dataMessage);

        Buffer buffer = Buffer.allocate();
        protocolEncoder.encode(dataMessage, buffer.asDataOutpuStream());
        return buffer;
    }

    void attachVersion(String context, String key, DataMessage dataMessage) {
        String compositKey = buildCompositeVersionKey(context, key);

        Version version = versionMap.get(compositKey);

        if (version == null)
            version = versionFactory.createNull();

        dataMessage.setVersion(version);
    }

    void updateVersion(String context, String key, DataMessage dataMessage) {
        String compositKey = buildCompositeVersionKey(context, key);
        
        versionMap.put(compositKey, dataMessage.getVersion());
    }

    private String buildCompositeVersionKey(String context, String key) {
        String compositKey = new StringBuilder().append(context).append(VERSION_COMPOSITE_KEY_DIVIDER)
                .append(key).toString();
        return compositKey;
    }

    private ResponseMessage sendMessage(Identity identity,
            RequestMessage requestMessage) throws Exception {
        return messenger.sendMessage(identity, nodePartition, requestMessage);
    }

    @Override
    public synchronized void shutdown() throws Exception {
        try {
            poller.shutdown();
        } finally {
            running.set(false);
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
