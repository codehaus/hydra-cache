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
package org.hydracache.client.transport;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;
import org.hydracache.io.Buffer;

/**
 * HTTP-based implementation of the message transport.
 * 
 * @author Tan Quach
 * @see <a href="http://www.hydracache.org/project/wiki/Protocol">Hydra Cache Protocol</a>
 * @since 1.0
 */
public class HttpTransport implements Transport
{
    private static final Logger log = Logger.getLogger(HttpTransport.class);
    
    private HttpClient httpClient;
    
    private final Map<Integer, ResponseMessageHandler> handlers = new HashMap<Integer, ResponseMessageHandler>();
    
    public HttpTransport() {
        // FIXME Make this more IoC and configurable.
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
        connectionManagerParams.setDefaultMaxConnectionsPerHost(10);
        connectionManagerParams.setMaxTotalConnections(100);
        connectionManager.setParams(connectionManagerParams);

        this.httpClient = new HttpClient(connectionManager);
    }

    @Override
    public Transport establishConnection(String hostName, int port)
    {
        HostConfiguration hostConfiguration = new HostConfiguration();
        hostConfiguration.setHost(hostName, port);

        this.httpClient.setHostConfiguration(hostConfiguration);
        return this;
    }
    
    @Override
    public ResponseMessage sendRequest(RequestMessage requestMessage) throws Exception
    {
        if (httpClient == null)
            throw new IllegalStateException("Establish connection first.");

        HttpMethod getMethod = getMethod(requestMessage);
        try {
            int responseCode = httpClient.executeMethod(getMethod);
            if (responseCode == HttpStatus.SC_NOT_FOUND)
                return null;

            ResponseMessageHandler handler = handlers.get(responseCode);
            
            return (handler == null ? null : handler.accept(responseCode, getMethod.getResponseBody()));
        } finally {
            getMethod.releaseConnection();
        }
    }

    /**
     * @param path
     * @return
     */
    private HttpMethod getMethod(RequestMessage requestMessage) {
        String action = requestMessage.getMethod();
        
        HttpMethod method = null;
        String uri = httpClient.getHostConfiguration().getHostURL() + "/" + requestMessage.getPath();

        if (action.equals("put")) {
            
            method = new PutMethod(uri);
            Buffer buffer = (Buffer) requestMessage.getRequestData();
            RequestEntity requestEntity = new InputStreamRequestEntity(buffer.asDataInputStream());

            ((PutMethod) method).setRequestEntity(requestEntity );
        } else {
            method = new GetMethod(uri);
        }
        return method;
        
    }

    /* (non-Javadoc)
     * @see com.ganz.wjr.external.transport.Transport#cleanUpConnections()
     */
    @Override
    public void cleanUpConnection()
    {
        this.httpClient.setHostConfiguration(null);
    }

    /* (non-Javadoc)
     * @see org.hydracache.client.transport.Transport#registerHandler(int, org.hydracache.client.transport.ConflictStatusHandler)
     */
    @Override
    public void registerHandler(Integer statusCode, ResponseMessageHandler handler) {
        handlers.put(statusCode, handler);
    }
}
