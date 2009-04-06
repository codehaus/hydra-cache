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
package org.hydracache.client.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.hydracache.client.HydraCacheClient;
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
import org.hydracache.server.data.versioning.VersionConflictException;
import org.hydracache.server.harmony.core.Substance;

/**
 * Simple HTTP-based client that connects to a node partition.
 * 
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public class PartitionAwareClient implements HydraCacheClient {
    private static Logger log = Logger
            .getLogger(PartitionAwareClient.class);

    private static final String PROTOCOL = "http://";

    private final NodePartition<Identity> nodePartition;

    private HttpClient httpClient;

    private ProtocolEncoder<DataMessage> protocolEncoder;

    private ProtocolDecoder<DataMessage> protocolDecoder;
    
    // FIXME: use a weak but thread safe map here to avoid memory leak
    private ConcurrentMap<String, Version> versionMap;

    /**
     * Construct a client instance referencing an existing {@link NodePartition}
     * .
     * 
     * @param connection
     *            The connection to the {@link Substance}.
     */
    public PartitionAwareClient(NodePartition<Identity> nodePartition) {
        this.nodePartition = nodePartition;
        
        createHttpClient();

        IncrementVersionFactory versionMarshaller = new IncrementVersionFactory();
        versionMarshaller.setIdentityMarshaller(new IdentityMarshaller());

        protocolEncoder = new DefaultProtocolEncoder(
                new MessageMarshallerFactory(versionMarshaller));

        protocolDecoder = new DefaultProtocolDecoder(
                new MessageMarshallerFactory(versionMarshaller));
        
        versionMap = new ConcurrentHashMap<String, Version>();
    }

    private void createHttpClient() {
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
        connectionManagerParams.setDefaultMaxConnectionsPerHost(10);
        connectionManagerParams.setMaxTotalConnections(100);
        connectionManager.setParams(connectionManagerParams);
        
        this.httpClient = new HttpClient(connectionManager);
    }

    /**
     * @param httpClient
     *            the httpClient to set
     */
    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#get(java.lang.String)
     */
    @Override
    public Object get(String key) throws IOException {
        Identity identity = nodePartition.get(key);
        String uri = constructUri(key, identity);

        Object object = null;

        GetMethod getMethod = new GetMethod(uri);

        try {
            int responseCode = httpClient.executeMethod(getMethod);
            log.debug("GET response code: " + responseCode);
            if (responseCode == HttpStatus.SC_NOT_FOUND)
                return null;
            validateGetResponseCode(responseCode);
            object = retrieveResultFromGet(key, getMethod);
        } finally {
            getMethod.releaseConnection();
        }

        return object;
    }

    String constructUri(String key, Identity identity) {
        StringBuffer uri = new StringBuffer();
        uri.append(PROTOCOL).append(identity.getAddress().getHostName())
                .append(":").append(identity.getPort()).append("/").append(key);
        return uri.toString();
    }

    void validateGetResponseCode(int responseCode) throws IOException {
        handleUnsuccessfulHttpStatus(responseCode);
    }

    Object retrieveResultFromGet(String key, GetMethod getMethod) throws IOException {
        Object object;
        DataMessage dataMessage = protocolDecoder.decode(new DataInputStream(
                getMethod.getResponseBodyAsStream()));
        updateVersion(key, dataMessage);
        object = SerializationUtils.deserialize(dataMessage.getBlob());
        return object;
    }

    // FIXME: implement weak map to avoid memory leak
    private void updateVersion(String key, DataMessage dataMessage) {
        versionMap.put(key, dataMessage.getVersion());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#put(java.lang.String,
     * java.io.Serializable)
     */
    @Override
    public void put(String key, Serializable data) throws IOException,
            VersionConflictException {
        Identity identity = nodePartition.get(key);
        String uri = constructUri(key, identity);

        PutMethod putMethod = new PutMethod(uri);

        try {
            RequestEntity requestEntity = buildRequestEntity(key, data, identity);
            putMethod.setRequestEntity(requestEntity);
            int responseCode = httpClient.executeMethod(putMethod);
            log.debug("PUT response code: " + responseCode);
            validatePutResponseCode(responseCode);
        } finally {
            putMethod.releaseConnection();
        }
    }

    RequestEntity buildRequestEntity(String key, Serializable data, Identity identity)
            throws IOException {
        Buffer buffer = encodePutData(key, data, identity);

        RequestEntity requestEntity = new InputStreamRequestEntity(buffer
                .asDataInputStream());

        return requestEntity;
    }

    private Buffer encodePutData(String key, Serializable data, Identity identity)
            throws IOException {
        Buffer buffer = Buffer.allocate();

        DataMessage dataMessage = new DataMessage();
        dataMessage.setBlob(SerializationUtils.serialize(data));

        Version version = versionMap.get(key);
        
        if(version == null)
            version = new IncrementVersionFactory().create(identity);
        
        dataMessage.setVersion(version);

        protocolEncoder.encode(dataMessage, buffer.asDataOutpuStream());

        return buffer;
    }

    void validatePutResponseCode(int responseCode) throws IOException,
            VersionConflictException {
        handleConflictHttpStatus(responseCode);
        handleUnsuccessfulHttpStatus(responseCode);
    }

    private void handleUnsuccessfulHttpStatus(int responseCode)
            throws IOException {
        if (responseCode != HttpStatus.SC_OK
                && responseCode != HttpStatus.SC_CREATED)
            throw new IOException("Error HTTP response received: "
                    + responseCode);
    }

    private void handleConflictHttpStatus(int responseCode)
            throws VersionConflictException {
        if (responseCode == HttpStatus.SC_CONFLICT)
            throw new VersionConflictException(
                    "Version conflict detected, please refresh your local cache");
    }

}
