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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
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
import org.hydracache.server.harmony.core.Substance;

/**
 * Simple HTTP-based client that connects to a node partition.
 * 
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public class PartitionAwareHydraCacheClient implements HydraCacheClient {

    private static final String PROTOCOL = "http://";

    private static final Logger logger = Logger
            .getLogger(PartitionAwareHydraCacheClient.class);

    private final NodePartition<Identity> nodePartition;

    private HttpClient httpClient;

    private ProtocolEncoder<DataMessage> protocolEncoder;

    private ProtocolDecoder<DataMessage> protocolDecoder;

    /**
     * Construct a client instance referencing an existing {@link NodePartition}
     * .
     * 
     * @param connection
     *            The connection to the {@link Substance}.
     */
    public PartitionAwareHydraCacheClient(NodePartition<Identity> nodePartition) {
        this.nodePartition = nodePartition;
        this.httpClient = new HttpClient();

        IncrementVersionFactory versionMarshaller = new IncrementVersionFactory();
        versionMarshaller.setIdentityMarshaller(new IdentityMarshaller());

        protocolEncoder = new DefaultProtocolEncoder(
                new MessageMarshallerFactory(versionMarshaller));

        protocolDecoder = new DefaultProtocolDecoder(
                new MessageMarshallerFactory(versionMarshaller));
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
    public Object get(String key) {
        Identity identity = nodePartition.get(key);
        String uri = constructUri(key, identity);

        Object object = null;

        try {
            GetMethod getMethod = new GetMethod(uri);

            int responseCode = httpClient.executeMethod(getMethod);

            validateResponseCode(responseCode);

            object = retrieveResultFromGet(getMethod);

        } catch (IOException ioe) {
            logger.error("Cannot retrieve data from cache.", ioe);
        }

        return object;
    }

    String constructUri(String key, Identity identity) {
        StringBuffer uri = new StringBuffer();
        uri.append(PROTOCOL).append(identity.getAddress().getHostName())
                .append(":").append(identity.getPort()).append("/").append(key);
        return uri.toString();
    }

    Object retrieveResultFromGet(GetMethod getMethod) throws IOException {
        Object object;
        DataMessage dataMessage = protocolDecoder.decode(new DataInputStream(
                getMethod.getResponseBodyAsStream()));
        // TODO: track version here
        object = SerializationUtils.deserialize(dataMessage.getBlob());        
        return object;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.client.HydraCacheClient#put(java.lang.String,
     * java.io.Serializable)
     */
    @Override
    public void put(String key, Serializable data) {
        Identity identity = nodePartition.get(key);
        String uri = constructUri(key, identity);

        try {
            PutMethod putMethod = new PutMethod(uri);

            RequestEntity requestEntity = buildRequestEntity(data, identity);

            putMethod.setRequestEntity(requestEntity);

            validateResponseCode(httpClient.executeMethod(putMethod));
        } catch (IOException ioe) {
            logger.error("Cannot write to cache.", ioe);
        }
    }

    RequestEntity buildRequestEntity(Serializable data,
            Identity identity) throws IOException {
        Buffer buffer = encodePutData(data, identity);

        RequestEntity requestEntity = new InputStreamRequestEntity(buffer
                .asDataInputStream());

        return requestEntity;
    }

    private Buffer encodePutData(Serializable data, Identity identity)
            throws IOException {
        Buffer buffer = Buffer.allocate();

        DataMessage dataMessage = new DataMessage();
        dataMessage.setBlob(SerializationUtils.serialize(data));
        // FIXME: put last known version here instead of new version each time
        dataMessage.setVersion(new IncrementVersionFactory().create(identity));

        protocolEncoder.encode(dataMessage, buffer.asDataOutpuStream());
        return buffer;
    }

    /**
     * Validate the response code
     * 
     * @param executeMethod
     * @throws IOException
     */
    private void validateResponseCode(int rc) throws IOException {
        if (rc != HttpStatus.SC_OK && rc != HttpStatus.SC_CREATED)
            throw new IOException("Error HTTP response received: " + rc);
    }

}
