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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.log4j.Logger;
import org.hydracache.client.HydraCacheClient;
import org.hydracache.data.hashing.HashFunction;
import org.hydracache.data.hashing.NativeHashFunction;
import org.hydracache.data.partitioning.NodePartition;
import org.hydracache.io.Buffer;
import org.hydracache.protocol.data.codec.DefaultProtocolDecoder;
import org.hydracache.protocol.data.codec.DefaultProtocolEncoder;
import org.hydracache.protocol.data.codec.ProtocolDecoder;
import org.hydracache.protocol.data.codec.ProtocolEncoder;
import org.hydracache.protocol.data.marshaller.MessageMarshallerFactory;
import org.hydracache.protocol.data.message.BlobDataMessage;
import org.hydracache.protocol.data.message.DataMessage;
import org.hydracache.server.Identity;
import org.hydracache.server.IdentityMarshaller;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.versioning.IncrementVersionFactory;
import org.hydracache.server.harmony.core.Substance;

/**
 * Simple HTTP-based client that connects to a node partition.
 * 
 * @author Tan Quach (tquach@jointsource.com)
 * @since 1.0
 */
public class HttpHydraCacheClient implements HydraCacheClient {

    private static final String PROTOCOL = "http://";

    private static final Logger logger = Logger
            .getLogger(HttpHydraCacheClient.class);

    private final NodePartition<Identity> nodePartition;

    private HashFunction hashFunction;

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
    public HttpHydraCacheClient(NodePartition<Identity> nodePartition) {
        this.nodePartition = nodePartition;
        this.hashFunction = new NativeHashFunction();
        this.httpClient = new HttpClient(
                new MultiThreadedHttpConnectionManager());

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

    /**
     * @param hashFunction
     *            the hashFunction to set
     */
    public void setHashFunction(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.connector.HydraCacheClient#get(java.lang.String)
     */
    @Override
    public Data get(Object key) {
        Identity identity = nodePartition.get(key);
        String uri = constructUri(key, identity);

        Data data = null;
        try {
            GetMethod getMethod = new GetMethod(uri);
            validateResponseCode(httpClient.executeMethod(getMethod));
            DataMessage dataMessage = protocolDecoder
                    .decode(new DataInputStream(getMethod
                            .getResponseBodyAsStream()));
            data = dataMessage.getData();
        } catch (IOException ioe) {
            logger.error("Cannot retrieve data.", ioe);
        }
        return data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.connector.HydraCacheClient#put(java.lang.String,
     * org.hydracache.server.data.storage.Data)
     */
    @Override
    public void put(Object key, Data data) {
        Identity identity = nodePartition.get(key);
        String uri = constructUri(key, identity);

        try {
            PutMethod putMethod = new PutMethod(uri);
            Buffer buffer = Buffer.allocate();
            protocolEncoder.encode(constructProtocolMessage(data), buffer.asDataOutpuStream());

            RequestEntity requestEntity = new InputStreamRequestEntity(
                    buffer.asDataInputStream());
            putMethod.setRequestEntity(requestEntity);

            validateResponseCode(httpClient.executeMethod(putMethod));
        } catch (IOException ioe) {
            logger.error("Cannot write to connection.", ioe);
        }
    }

    /**
     * @param data
     * @return
     */
    private DataMessage constructProtocolMessage(Data data) {
        BlobDataMessage dataMessage = new BlobDataMessage();
        dataMessage.setBlob(data.getContent());
        dataMessage.setKeyHash(data.getKeyHash());
        dataMessage.setVersion(data.getVersion());
        return dataMessage;
    }

    /**
     * Validate the response code
     * 
     * @param executeMethod
     * @throws IOException
     */
    private void validateResponseCode(int rc) throws IOException {
        if (rc != HttpStatus.SC_OK && rc != HttpStatus.SC_CREATED)
            throw new IOException("HTTP Response: " + rc);
    }

    /**
     * @param key
     * @param identity
     * @return
     */
    private String constructUri(Object key, Identity identity) {
        StringBuffer uri = new StringBuffer();
        uri.append(PROTOCOL).append(identity.getAddress().getHostName())
                .append(":").append(identity.getPort()).append("/").append(
                        hashFunction.hash(key));
        return uri.toString();
    }

}
