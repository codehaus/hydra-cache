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
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.hydracache.client.InternalHydraException;
import org.hydracache.io.Buffer;

/**
 * HTTP-based implementation of the message transport.
 * 
 * @author Tan Quach
 * @see <a href="http://www.hydracache.org/project/wiki/Protocol">Hydra Cache
 *      Protocol</a>
 * @since 1.0
 */
public class HttpTransport implements Transport {
    HttpClient httpClient;

    private final Map<Integer, ResponseMessageHandler> handlers = new HashMap<Integer, ResponseMessageHandler>();

    public HttpTransport() {
        // FIXME Make this more IoC and configurable.
        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
        connectionManagerParams.setDefaultMaxConnectionsPerHost(10);
        connectionManagerParams.setMaxTotalConnections(100);
        connectionManager.setParams(connectionManagerParams);

        this.httpClient = new HttpClient(connectionManager);
    }

    @Override
    public Transport establishConnection(final String hostName, final int port) {
        final HostConfiguration hostConfiguration = new HostConfiguration();
        hostConfiguration.setHost(hostName, port);

        httpClient.setHostConfiguration(hostConfiguration);

        return this;
    }

    @Override
    public ResponseMessage sendRequest(final RequestMessage requestMessage) throws Exception {
        if (httpClient == null) {
            throw new IllegalStateException("Establish connection first.");
        }

        final HttpMethod httpMethod = createHttpMethod(requestMessage);

        try {
            final int responseCode = httpClient.executeMethod(httpMethod);

            if (responseCode == HttpStatus.SC_NOT_FOUND) {
                return null;
            } else if (responseCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                throw new InternalHydraException(httpMethod.getResponseBodyAsString());
            } else {
                final ResponseMessageHandler handler = handlers.get(responseCode);

                return (handler == null ? null : handler.accept(responseCode,
                        httpMethod.getResponseBody()));
            }
        } finally {
            httpMethod.releaseConnection();
        }
    }

    HttpMethod createHttpMethod(final RequestMessage requestMessage) {
        final String action = requestMessage.getMethod();

        HttpMethod method = null;

        String uri = "";

        if (httpClient.getHostConfiguration() != null) {
            uri = httpClient.getHostConfiguration().getHostURL();
        }

        if (StringUtils.isNotBlank(requestMessage.getContext())) {
            uri += "/" + requestMessage.getContext();
        }

        uri += "/" + requestMessage.getPath();

        if ("put".equalsIgnoreCase(action)) {
            method = new PutMethod(uri);
            final Buffer buffer = (Buffer) requestMessage.getRequestData();

            if (buffer != null) {
                final RequestEntity requestEntity = new InputStreamRequestEntity(
                        buffer.asDataInputStream());

                ((PutMethod) method).setRequestEntity(requestEntity);
            }
        } else if ("delete".equalsIgnoreCase(action)) {
            method = new DeleteMethod(uri);
        } else {
            method = new GetMethod(uri);
        }

        return method;
    }

    @Override
    public void cleanUpConnection() {
        this.httpClient.setHostConfiguration(null);
    }

    @Override
    public void registerHandler(final Integer statusCode, final ResponseMessageHandler handler) {
        handlers.put(statusCode, handler);
    }
}
